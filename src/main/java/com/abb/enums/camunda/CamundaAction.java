package com.abb.enums.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum CamundaAction {
  PD_APP_CLOSE("pd:app:close"),
  PD_APP_REWORK("pd:app:rework"),
  PD_APP_ALLOCATE("pd:app:allocate"),
  PD_APP_REASSIGN("pd:app:reassign"),
  PD_APP_REASSIGN_TEAM_LEAD("pd:app:reassign:team:lead"),
  PD_APP_ENDORSE("pd:app:endorse");

  private final String value;

  public static CamundaAction get(String value) {
    return Arrays.stream(CamundaAction.values()).filter(e -> StringUtils.equals(e.getValue(), value))
        .findFirst().orElse(null);
  }
}
