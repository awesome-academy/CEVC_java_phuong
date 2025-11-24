package com.foodorder.foodapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class EnvProperties {
  private String folder;
  private String location;
}
