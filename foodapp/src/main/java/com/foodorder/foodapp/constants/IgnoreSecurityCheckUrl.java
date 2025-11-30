package com.foodorder.foodapp.constants;

import java.util.Arrays;
import java.util.List;

public class IgnoreSecurityCheckUrl {

  public static final List<String> PUBLIC_API_URLS = Arrays.asList(
      "/api/register-user",
      "/api/categories",
      "/api/products/**",
      "/api/product-types",
      "/api/auth/**",
      "/api/users/register");

  public static final List<String> PUBLIC_STATIC_URLS = Arrays.asList(
      "/css/**",
      "/js/**",
      "/public/**",
      "/images/**",
      "/swagger-ui/**",
      "/v3/api-docs/**");
  public static final List<String> PUBLIC_JWT_URLS = Arrays.asList(
      "/admin/**");
}
