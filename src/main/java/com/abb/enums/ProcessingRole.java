package com.abb.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ProcessingRole {
  PD_RB_RM,
  PD_RB_BM,
  PD_RB_CA1,
  PD_RB_CA2,
  PD_RB_CA3,
  PD_RB_CONTACT_LEAD,
  PD_RB_CONTACT,
  PD_RB_CC_CONTROL,
  PD_RB_CC1,
  PD_RB_CC2,
  PD_RB_CC3,
  PD_RB_FI,
  PD_RB_UNDEFINED,
  ;

  private static final Set<ProcessingRole> ROLE_CIC = new HashSet<>(Arrays.asList(
      PD_RB_RM,
      PD_RB_BM));

  public static boolean isCICRole(String role) {
    if (!StringUtils.hasText(role)) {
      return false;
    }
    ProcessingRole processingRole;
    try {
      processingRole = valueOf(role);
    } catch (Exception ex) {
      return false;
    }
    return ROLE_CIC.contains(processingRole);
  }

  public static boolean isRoleApproval(String processingRole) {
    switch (ProcessingRole.valueOf(processingRole)) {
      case PD_RB_CA1:
      case PD_RB_CA2:
      case PD_RB_CA3:
      case PD_RB_CC_CONTROL:
      case PD_RB_CC1:
      case PD_RB_CC2:
      case PD_RB_CC3:
        return true;
      default:
        return false;
    }
  }

  /**
   * Các role xử lý hồ sơ không cần refresh trạng thái tích xanh
   *
   * @param processingRole  String
   * @return  Boolean
   */
  public static boolean isNotRefreshTabStatus(String processingRole) {
    return Arrays.asList(PD_RB_CONTACT_LEAD.name(), PD_RB_CONTACT.name()).contains(processingRole);
  }

  public static String isOrganizationStep(String processingRole) {
    return Arrays.asList(PD_RB_CC_CONTROL.name(), PD_RB_CC1.name(), PD_RB_CC2.name(), PD_RB_CC3.name())
        .contains(processingRole) ? "L001" : "";
  }

  public static boolean isRequiredEffectivePeriod(String processingRole) {
    return Arrays.asList(PD_RB_CA1.name(), PD_RB_CA2.name(), PD_RB_CA3.name(),
            PD_RB_CC_CONTROL.name(), PD_RB_CC1.name(), PD_RB_CC2.name(), PD_RB_CC3.name())
        .contains(processingRole);
  }

  public static boolean isRequiredCicResult(String processingRole) {
    return Arrays.asList(PD_RB_CA1.name(), PD_RB_CA2.name(), PD_RB_CA3.name(),
            PD_RB_CC_CONTROL.name(), PD_RB_CC1.name(), PD_RB_CC2.name(), PD_RB_CC3.name())
        .contains(processingRole);
  }

  public static boolean isRequiredOpRiskCollateralResult(String processingRole) {
    return Arrays.asList(PD_RB_RM.name(), PD_RB_CA1.name())
        .contains(processingRole);
  }

}
