package com.abb.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "camunda.bpm.client")
@Getter
@Setter
public class CamundaProperties {

  private Map<String, ExternalTaskSubscription> subscriptions;
}
