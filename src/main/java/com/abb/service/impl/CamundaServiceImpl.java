package com.abb.service.impl;

import com.abb.client.CamundaClient;
import com.abb.client.CamundaProperties;
import com.abb.domain.ProcessInstanceEntity;
import com.abb.enums.camunda.CamundaTopic;
import com.abb.enums.camunda.CamundaVariable;
import com.abb.exception.CamundaUtilException;
import com.abb.exception.DomainCode;
import com.abb.service.CamundaService;
import com.abb.utils.CamundaUtil;
import com.abb.utils.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.TaskDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.abb.enums.ProcessingRole.PD_RB_RM;

@Service
@Slf4j
@RequiredArgsConstructor
public class CamundaServiceImpl implements CamundaService {
    private final CamundaClient camundaClient;
    private final ObjectMapper objectMapper;
    private final CamundaProperties properties;

    @SneakyThrows
    @Override
    public ProcessInstanceWithVariablesDto startProcessInstance(String requestCode, String processDefinitionKey, Integer version) {
        Map<String, VariableValueDto> variables = new HashMap<>();
        variables.put(CamundaVariable.REQUEST_CODE.getValue(), new VariableValueDto().value(requestCode));

        ProcessInstanceWithVariablesDto processInstanceWithVariablesDto = camundaClient
            .startProcessInstance(processDefinitionKey, version, variables, true);

        log.info(">>>> Workflow process {} is started, process instance id {}", processDefinitionKey,
            processInstanceWithVariablesDto.getId());

        TaskDto nextTask = camundaClient.getTask(
            Collections.singletonList(processInstanceWithVariablesDto.getId()),
            processInstanceWithVariablesDto.getBusinessKey());

        log.info(">>>> Camunda workflow next task {}", nextTask);

        processInstanceWithVariablesDto.getVariables()
            .put("nextTask", new VariableValueDto().value(nextTask));

        return processInstanceWithVariablesDto;
    }

    @SneakyThrows
    @Override
    public VariableValueDto getCurrentState(String processingRole, ProcessInstanceEntity processInstanceEntity, String varName) {
        VariableValueDto variable;

        String processInstanceId;
        if (1 == processInstanceEntity.getWorkflowVersion()) {
            processInstanceId = PD_RB_RM.name().equalsIgnoreCase(processingRole)
                ? processInstanceEntity.getProcessInstanceId()
                : processInstanceEntity.getSubProcessInstanceId();
        } else {
            processInstanceId = StringUtils.isNotBlank(processInstanceEntity.getSubProcessInstanceId())
                ? processInstanceEntity.getSubProcessInstanceId()
                : processInstanceEntity.getProcessInstanceId();
        }

        try {
            variable = camundaClient.getProcessVariables(processInstanceId, varName);
        } catch (Exception e) {
            log.error(">>>> Can't get current state of process instance {} , {}", processInstanceId,
                e.getMessage());
            variable = camundaClient.getProcessHistoryVariables(processInstanceId, varName);
        }

        log.info(">>>> Current state of process instance {} , {}", processInstanceId, variable);

        return variable;
    }

    @SneakyThrows
    @Override
    public Map<String, VariableValueDto> completeTaskWithReturnVariables(Object data,
                                                                         String status,
                                                                         ProcessInstanceEntity processInstanceEntity,
                                                                         String processInstanceId,
                                                                         String nextStepCode) {
        log.info(">>>> data : {} submit task with processInstanceId : {} , nextStepCode : {}",
            data, processInstanceId,
            nextStepCode);


        Map<String, VariableValueDto> response;


        boolean isOldVersion = 1 == processInstanceEntity.getWorkflowVersion();

        log.info(">>>> Camunda workflow is old version : {}", isOldVersion);

        // Lấy lại trạng thái hiện tại của camunda workflow
        String role = "";
        VariableValueDto varState = getCurrentState(role,
            processInstanceEntity,
            CamundaVariable.STATUS.getValue());

        log.info(">>>> Camunda workflow current state : {}", varState.getValue());

        if (isOldVersion) {
            if (Objects.equals(status, varState.getValue())) {
                List<String> processInstanceIds = new ArrayList<>(Collections.singletonList(
                    processInstanceEntity.getProcessInstanceId()));

                String processKey = properties.getSubscriptions().get(
                    CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
                ProcessInstanceDto currentProcess = camundaClient.getProcessInstanceByProcessKey(
                    processInstanceEntity.getProcessBusinessKey(), processKey);

                if (currentProcess != null) {
                    processInstanceIds.add(currentProcess.getId());
                }

                response = completeTaskWithReturnVariables(processInstanceIds,
                    processInstanceEntity.getProcessBusinessKey(),
                    CamundaUtil.buildNextStepVariables(nextStepCode));
            } else {
                throw new CamundaUtilException(DomainCode.CAMUNDA_STATUS_ERROR);
            }
        } else {
            response = completeTaskWithReturnVariablesNewVersion(data,status, processInstanceEntity, processInstanceId, varState, nextStepCode);
        }

        return response;
    }


    @SneakyThrows
    private Map<String, VariableValueDto> completeTaskWithReturnVariables(List<String> processInstanceIds,
                                                                          String processBusinessKey,
                                                                          Map<String, VariableValueDto> variables) {
        TaskDto currentTask = camundaClient.getTask(processInstanceIds, processBusinessKey);

        Map<String, VariableValueDto> variableMapResponse = camundaClient.submitForm(
            currentTask.getId(), variables, Boolean.TRUE);

        log.info(">>>> Camunda task {} is completed with variable [{}]", currentTask.getId(), variables);

        return variableMapResponse;
    }

    @SneakyThrows
    public Map<String, VariableValueDto> completeTaskWithReturnVariablesNewVersion(Object data,
                                                                                   String status,
                                                                                   ProcessInstanceEntity processInstanceEntity,
                                                                                   String processInstanceId,
                                                                                   VariableValueDto varState,
                                                                                   String nextStepCode) {

        Map<String, VariableValueDto> response;

        if (Objects.equals(status, varState.getValue())) {
            response = camundaClient.submitForm(processInstanceEntity.getTaskId(),
                CamundaUtil.buildNextStepVariables(nextStepCode), Boolean.TRUE);

            log.info(">>>> Camunda workflow next state then submit task : {}",
                CamundaUtil.getStatus(response));

            TaskDto taskDto;
            if (ObjectUtils.isEmpty(CamundaUtil.getStatus(response))) {
                taskDto = new TaskDto().id(CamundaVariable.END_TASK.getValue()).taskDefinitionKey(CamundaVariable.END_TASK.getValue());
            } else {

                String processKey = properties.getSubscriptions().get(
                    CamundaTopic.INITIALIZE_VARIABLE.name()).getProcessDefinitionKey();
                ProcessInstanceDto currentProcess = camundaClient.getProcessInstanceByProcessKey(
                    processInstanceEntity.getProcessBusinessKey(), processKey);

                log.info(">>>> Camunda workflow current processInstance : {}",
                    JsonUtil.convertObject2String(currentProcess, objectMapper));

                List<String> processInstanceIds = new ArrayList<>();
                processInstanceIds.add(processInstanceEntity.getProcessInstanceId());
                if (currentProcess != null) {
                    processInstanceIds.add(currentProcess.getId());
                    processInstanceEntity.setSubProcessInstanceId(currentProcess.getId());
                    processInstanceEntity.setSubProcessKey(processKey);
                    processInstanceEntity
                        .setSubProcessDefinitionId(currentProcess.getDefinitionId());
                } else {
                    processInstanceEntity.setSubProcessInstanceId(null);
                    processInstanceEntity.setSubProcessDefinitionId(null);
                }

                taskDto = camundaClient.getTask(processInstanceIds,
                    processInstanceEntity.getProcessBusinessKey());

                log.info(">>>> Camunda workflow next task : {}",
                    JsonUtil.convertObject2String(taskDto, objectMapper));
            }

            processInstanceEntity.setTaskId(taskDto.getId());
            processInstanceEntity.setTaskDefinitionKey(taskDto.getTaskDefinitionKey());

            response.put("currentTask",
                new VariableValueDto().value(JsonUtil.convertObject2String(taskDto, objectMapper)));

        } else {
            throw new CamundaUtilException(DomainCode.CAMUNDA_STATUS_ERROR);
        }

        return response;
    }
}
