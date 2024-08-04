package com.abb.enums.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CamundaVariable {
  STATUS("status"),
  ACTION("action"),
  TRANSFER_TO("transferTo"),
  NEXT_STEP("nextStep"),
  WORKFLOW_VERSION("workflowVersion"),
  SYNC_STATE("syncState"),
  REQUEST_CODE("requestCode"),
  END_TASK("End");

  private final String value;
}
