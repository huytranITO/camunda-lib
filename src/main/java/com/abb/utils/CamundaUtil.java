package com.abb.utils;

import io.jsonwebtoken.lang.Collections;
import lombok.experimental.UtilityClass;
import org.camunda.community.rest.client.dto.VariableValueDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.abb.enums.camunda.CamundaVariable.*;

@UtilityClass
public class CamundaUtil {

  public Map<String, VariableValueDto> buildActionVariables(String action) {
    Map<String, VariableValueDto> variables = new HashMap<>();
    variables.put(ACTION.getValue(), new VariableValueDto().value(action));
    return variables;
  }

  public Map<String, VariableValueDto> buildVariables(String... values) {
    Map<String, VariableValueDto> variables = new HashMap<>();
    variables.put(ACTION.getValue(), new VariableValueDto().value(values[0]));
    variables.put(STATUS.getValue(), new VariableValueDto().value(values[1]));
    variables.put(NEXT_STEP.getValue(), new VariableValueDto().value(values[2]));
    return variables;
  }

  public Map<String, VariableValueDto> buildNextStepVariables(String nextStep) {
    Map<String, VariableValueDto> variables = new HashMap<>();
    variables.put(NEXT_STEP.getValue(), new VariableValueDto().value(nextStep));
    return variables;
  }

  public String getStatus(Map<String, VariableValueDto> returnVar) {
    if (Collections.isEmpty(returnVar)) {
      return null;
    }

    VariableValueDto statusVariable = returnVar.get(STATUS.getValue());

    if (Objects.nonNull(statusVariable)) {
      return (String) statusVariable.getValue();
    }

    return null;
  }

  public Integer getWorkflowVersion(Map<String, VariableValueDto> returnVar) {
    if (Collections.isEmpty(returnVar)) {
      return null;
    }

    VariableValueDto statusVariable = returnVar.get(WORKFLOW_VERSION.getValue());

    if (Objects.nonNull(statusVariable)) {
      return (Integer) statusVariable.getValue();
    }

    return null;
  }

//  public ButtonEventCode getButtonEventCode(String action) {
//    if (StringUtils.isBlank(action)) {
//      return ButtonEventCode.SUCCESS;
//    }
//
//    switch (CamundaAction.get(action)) {
//      case PD_APP_REWORK:
//        return ButtonEventCode.REWORK;
//      case PD_APP_ALLOCATE:
//        return ButtonEventCode.ALLOCATE;
//      case PD_APP_REASSIGN:
//        return ButtonEventCode.REASSIGN;
//      case PD_APP_REASSIGN_TEAM_LEAD:
//        return ButtonEventCode.REASSIGN_TEAM_LEAD;
//      default:
//        return ButtonEventCode.SUCCESS;
//    }
//  }
}
