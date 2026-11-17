package com.foodorder.foodapp.config;

import java.util.Locale;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    // No locale in session: API reads Accept-Language (clients use tokens, no session),
    // admin pages default to English until changed with ?lang=
    slr.setDefaultLocaleFunction(request -> request.getRequestURI().startsWith(request.getContextPath() + "/api/")
        && request.getHeader("Accept-Language") != null
            ? request.getLocale()
            : Locale.ENGLISH);
    return slr;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Only admin pages switch language with ?lang= (stored in session); API must not create sessions
    registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/admin/**");
  }
}
