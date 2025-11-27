package com.foodorder.foodapp.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foodorder.foodapp.constants.IgnoreSecurityCheckUrl;

@Configuration
public class IgnoreSecurityConfig {

  private final EnvProperties envProperties;

  public IgnoreSecurityConfig(EnvProperties envProperties) {
    this.envProperties = envProperties;
  }

  @Bean
  public List<String> publicStaticUrls() {
    List<String> urls = new ArrayList<>(IgnoreSecurityCheckUrl.PUBLIC_STATIC_URLS);
    urls.add(envProperties.getFolder() + "**");
    return urls;
  }

  @Bean
  public List<String> publicApiUrls() {
    List<String> urls = new ArrayList<>(IgnoreSecurityCheckUrl.PUBLIC_API_URLS);
    urls.addAll(IgnoreSecurityCheckUrl.PUBLIC_JWT_URLS);
    return urls;
  }
}
