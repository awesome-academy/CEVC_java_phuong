package com.foodorder.foodapp.config;

import com.foodorder.foodapp.config.properties.UploadProperties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foodorder.foodapp.constants.IgnoreSecurityCheckUrl;

@Configuration
public class IgnoreSecurityConfig {

  private final UploadProperties uploadProperties;

  public IgnoreSecurityConfig(UploadProperties uploadProperties) {
    this.uploadProperties = uploadProperties;
  }

  @Bean
  public List<String> publicStaticUrls() {
    List<String> urls = new ArrayList<>(IgnoreSecurityCheckUrl.PUBLIC_STATIC_URLS);
    urls.add(uploadProperties.folder() + "**");
    return urls;
  }

  @Bean
  public List<String> publicApiUrls() {
    List<String> urls = new ArrayList<>(IgnoreSecurityCheckUrl.PUBLIC_API_URLS);
    return urls;
  }
}
