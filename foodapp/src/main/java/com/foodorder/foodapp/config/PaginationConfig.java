package com.foodorder.foodapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.pagination")
public class PaginationConfig {
  private int defaultPage;
  private int defaultPerPage;
}
