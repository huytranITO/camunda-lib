package com.abb.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DomainCode {
    SUCCESS("AS-000", "Success", HttpStatus.OK.value()),
    INVALID_PARAMETER("AS-001", "Invalid parameter: %s", HttpStatus.BAD_REQUEST.value()),
    INTERNAL_SERVICE_ERROR("AS-002", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    EXTERNAL_SERVICE_CLIENT_ERROR("AS-003", "External service error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    EXTERNAL_SERVICE_SERVER_ERROR("AS-004", "External service error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    TIMEOUT_ERROR("AS-005", "Timeout error", HttpStatus.GATEWAY_TIMEOUT.value()),
    FORBIDDEN("AS-006", "Forbidden", HttpStatus.FORBIDDEN.value()),
    TOKEN_EXPIRED("400004", "TOKEN_EXPIRED: %s", HttpStatus.UNAUTHORIZED.value()),
    TYPE_UNSUPPORTED("AS-007", "This type is unsupported", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
    NOT_FOUND_APPLICATION("AS-008", "Not found application", HttpStatus.NOT_FOUND.value()),
    NOT_FOUND_CUSTOMER("AS-009", "Not found customer", HttpStatus.NOT_FOUND.value()),
    NOT_FOUND_APPLICATION_FIELD_INFORMATION("AS-010", "Not found application field information", HttpStatus.NOT_FOUND.value()),
    REQUEST_TYPE_NOT_ACCEPTED("AS-011", "Request type not accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),

    /**
     * Camunda error
     */
    CAMUNDA_SYSTEM_ERROR("CMD-999", "Camunda system error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    NOT_FOUND_CAMUNDA_PROCESS("CMD-001", "Not found camunda process", HttpStatus.NOT_FOUND.value()),
    NOT_FOUND_CAMUNDA_TASK_WAIT_MINUTE("CMD-002", "Not found camunda task, please wait a minute", HttpStatus.NOT_FOUND.value()),
    CAMUNDA_STATUS_ERROR("CMD-003", "Current status of profile is invalid, please contact system administrator!", HttpStatus.BAD_REQUEST.value()),

    /**
     * Matrix workflow
     */
    DONT_ALLOW_STEP_TRANSITIONS("WFL-001", "Don't allow step transitions", HttpStatus.BAD_REQUEST.value());


    private final String code;
    private final String message;
    private final int status;

    public static DomainCode get(String code) {
        return Arrays.stream(DomainCode.values()).filter(e -> StringUtils.equals(e.getCode(), code))
            .findFirst().orElse(null);
    }
}
