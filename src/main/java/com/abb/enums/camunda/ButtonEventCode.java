package com.abb.enums.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonEventCode {
  SUCCESS("0000"),
  REWORK("0001"),
  ALLOCATE("0002"),
  REASSIGN("0003"),
  REASSIGN_TEAM_LEAD("0004");

  private final String code;
}
