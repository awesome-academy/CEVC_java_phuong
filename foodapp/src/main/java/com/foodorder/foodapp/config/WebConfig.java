package com.foodorder.foodapp.config;

import com.foodorder.foodapp.config.properties.UploadProperties;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final UploadProperties uploadProperties;

  public WebConfig(UploadProperties uploadProperties) {
    this.uploadProperties = uploadProperties;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(uploadProperties.folder() + "**")
        .addResourceLocations("file:" + uploadProperties.location() + "/");
  }
}
