package com.foodorder.foodapp.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {
  private long maxSize;

  @Override
  public void initialize(FileSize constraintAnnotation) {
    this.maxSize = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null || file.isEmpty()) {
      return true;
    }
    return file.getSize() <= maxSize;
  }
}
