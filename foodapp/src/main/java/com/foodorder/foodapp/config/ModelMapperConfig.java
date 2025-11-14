package com.foodorder.foodapp.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foodorder.foodapp.service.FilesStorageService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {

  private final FilesStorageService filesStorageService;

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();

    mapper.getConfiguration()
        .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT)
        .setFieldMatchingEnabled(true)
        .setSkipNullEnabled(true)
        .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

    filesStorageService.init();

    return mapper;
  }
}
