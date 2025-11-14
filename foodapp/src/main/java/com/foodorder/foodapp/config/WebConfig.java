package com.foodorder.foodapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.upload.location}")
  private String uploadLocation;

  @Value("${app.upload.folder}")
  private String folder;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(folder + "**")
        .addResourceLocations("file:" + uploadLocation + "/");
  }
}
