package com.abb.service;

import com.abb.domain.ProcessInstanceEntity;
import org.camunda.community.rest.client.dto.ProcessInstanceWithVariablesDto;
import org.camunda.community.rest.client.dto.VariableValueDto;

import java.util.Map;

public interface CamundaService {
    ProcessInstanceWithVariablesDto startProcessInstance(String requestCode, String processDefinitionKey, Integer version);

    VariableValueDto getCurrentState(String processingRole, ProcessInstanceEntity processInstanceEntity, String varName);

    Map<String, VariableValueDto> completeTaskWithReturnVariables(Object data,
                                                                  String status,
                                                                  ProcessInstanceEntity processInstanceEntity,
                                                                  String processInstanceId,
                                                                  String nextStepCode);
}
