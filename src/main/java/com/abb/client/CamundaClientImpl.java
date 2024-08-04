package com.abb.client;

import com.abb.exception.CamundaUtilException;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.camunda.community.rest.client.api.HistoricVariableInstanceApi;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.api.TaskApi;
import org.camunda.community.rest.client.dto.*;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.abb.exception.DomainCode.NOT_FOUND_CAMUNDA_PROCESS;
import static com.abb.exception.DomainCode.NOT_FOUND_CAMUNDA_TASK_WAIT_MINUTE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamundaClientImpl implements CamundaClient {

  private final ProcessInstanceApi processInstanceApi;
  private final ProcessDefinitionApi processDefinitionApi;
  private final TaskApi taskApi;
  private final HistoricVariableInstanceApi historicVariableInstanceApi;

  @Override
  public ProcessInstanceWithVariablesDto startProcessInstance(String processKey, Integer version,
      Map<String, VariableValueDto> variables, boolean returnVariable) throws ApiException {
    ProcessDefinitionDto processDefinitionDto = getProcessDefinitionByKeyAndVersion(processKey,
        version);

    ProcessInstanceWithVariablesDto response = startProcessInstanceById(
        processDefinitionDto.getId(), variables, returnVariable);

    response.getVariables()
        .put("workflowVersion", new VariableValueDto().value(processDefinitionDto.getVersion()));
    return response;
  }

  @Override
  public Map<String, VariableValueDto> submitForm(String id,
      Map<String, VariableValueDto> variables, boolean returnVariable) throws ApiException {
    return taskApi.submit(id, new CompleteTaskDto().withVariablesInReturn(returnVariable)
        .variables(variables));
  }

  protected ProcessInstanceWithVariablesDto startProcessInstanceById(String processDefinitionId,
      Map<String, VariableValueDto> variables, boolean returnVariable) throws ApiException {
    StartProcessInstanceDto processInstanceDto = new StartProcessInstanceDto().businessKey(
        UUID.randomUUID().toString()).variables(variables).withVariablesInReturn(returnVariable);
    return processDefinitionApi.startProcessInstance(processDefinitionId, processInstanceDto);
  }

  @Override
  public ProcessDefinitionDto getProcessDefinitionByKeyAndVersion(String processKey,
      Integer version) throws ApiException {
    List<ProcessDefinitionDto> processDefinitions = processDefinitionApi.getProcessDefinitions(null,
        null, null, null, null, null, null,
        processKey, null, null, null, null, version, Objects.isNull(version), null, null, null,
        true, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);

    if (CollectionUtils.isEmpty(processDefinitions)) {
      throw new CamundaUtilException(NOT_FOUND_CAMUNDA_PROCESS, new Object[]{processKey});
    }

    return processDefinitions.get(0);
  }

  @Override
  public ProcessDefinitionDto getProcessDefinitionById(String processDefinitionId)
      throws ApiException {
    return processDefinitionApi.getProcessDefinition(processDefinitionId);
  }

  @Override
  public TaskDto getTask(List<String> processInstanceIds, String processBusinessKey)
      throws ApiException {
    List<TaskDto> tasks = taskApi.queryTasks(null, null, new TaskQueryDto()
        .processInstanceIdIn(processInstanceIds)
        .processInstanceBusinessKey(processBusinessKey));

    if (Collections.isEmpty(tasks)) {
      throw new CamundaUtilException(NOT_FOUND_CAMUNDA_TASK_WAIT_MINUTE);
    }

    return tasks.get(0);
  }

  @Override
  public ProcessInstanceDto getProcessInstance(String processKey, String processBusinessKey)
      throws ApiException {
    ProcessInstanceQueryDto queryDto = new ProcessInstanceQueryDto()
        .processDefinitionKey(processKey)
        .businessKey(processBusinessKey);

    List<ProcessInstanceDto> processInstances = processInstanceApi.queryProcessInstances(null, null,
        queryDto);

    if (Collections.isEmpty(processInstances)) {
      throw new CamundaUtilException(NOT_FOUND_CAMUNDA_PROCESS);
    }

    return processInstances.get(0);
  }

  @Override
  public ProcessInstanceDto getProcessInstance(List<String> processInstanceIds,
      String processBusinessKey) throws ApiException {
    ProcessInstanceQueryDto queryDto = new ProcessInstanceQueryDto()
        .processInstanceIds(processInstanceIds)
        .businessKey(processBusinessKey);

    List<ProcessInstanceDto> processInstances = processInstanceApi.queryProcessInstances(null, null,
        queryDto);

    if (Collections.isEmpty(processInstances)) {
      throw new CamundaUtilException(NOT_FOUND_CAMUNDA_PROCESS);
    }

    return processInstances.get(0);
  }

  @Override
  public ProcessInstanceDto getProcessInstanceById(String processInstanceId) throws ApiException {
    return processInstanceApi.getProcessInstance(processInstanceId);
  }

  @Override
  public VariableValueDto getProcessHistoryVariables(String processInstanceId,
      String varName) throws ApiException {
    List<HistoricVariableInstanceDto> historicVariables = historicVariableInstanceApi.queryHistoricVariableInstances(
        null, null, true, new HistoricVariableInstanceQueryDto()
            .processInstanceId(processInstanceId)
            .variableName(varName));

    VariableValueDto response = null;

    if (CollectionUtils.isNotEmpty(historicVariables)) {
      response = new VariableValueDto();
      HistoricVariableInstanceDto historicVariableInstance = historicVariables.get(0);
      response.setType(historicVariableInstance.getType());
      response.setValue(historicVariableInstance.getValue());
      response.setValueInfo(historicVariableInstance.getValueInfo());
    }

    return response;
  }

  @Override
  public ProcessInstanceDto getProcessInstanceByProcessKey(String processBusinessKey,
      String processKey) throws ApiException {
    ProcessInstanceQueryDto queryDto = new ProcessInstanceQueryDto()
        .active(Boolean.TRUE)
        .businessKey(processBusinessKey);

    List<ProcessInstanceDto> processInstances = processInstanceApi.queryProcessInstances(null, null,
        queryDto);

    if (Collections.isEmpty(processInstances)) {
      return null;
    }

    return processInstances.stream()
        .filter(item -> item.getDefinitionId().contains(processKey))
        .findFirst()
        .orElse(null);
  }

  @Override
  public VariableValueDto getProcessVariables(String processInstanceId, String varName)
      throws ApiException {
    return processInstanceApi.getProcessInstanceVariable(processInstanceId, varName, true);
  }
}
