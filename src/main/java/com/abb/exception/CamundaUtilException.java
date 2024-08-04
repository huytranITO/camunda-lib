package com.abb.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CamundaUtilException extends RuntimeException {

  private final DomainCode code;
  private transient Object[] args;
  private HttpStatus status;

  public CamundaUtilException(DomainCode code) {
    this.code = code;
  }

  public CamundaUtilException(DomainCode code, Object[] args) {
    this.code = code;
    this.args = args;
  }

  public CamundaUtilException(HttpStatus status, DomainCode code, Object[] args) {
    this.status = status;
    this.code = code;
    this.args = args;
  }

  public CamundaUtilException(HttpStatus status, DomainCode code) {
    this.status = status;
    this.code = code;
  }
}
