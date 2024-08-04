package com.abb.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalTaskSubscription {

  private String processDefinitionKey;
  private boolean includeExtensionProperties;
}
