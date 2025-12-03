package com.foodorder.foodapp.dto.evaluate;

import org.springframework.web.multipart.MultipartFile;

import com.foodorder.foodapp.validation.FileSize;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvaluateDTO {
  @NotBlank(message = "{validation.evaluate.content.required}")
  @Size(max = 1000, message = "{validation.evaluate.content.max}")
  private String content;

  @NotNull(message = "{validation.evaluate.rating.required}")
  @Min(value = 1, message = "{validation.evaluate.rating.min}")
  @Max(value = 5, message = "{validation.evaluate.rating.max}")
  private Integer rating;

  @FileSize(max = 2 * 1024 * 1024, message = "{validation.image.size}")
  private MultipartFile imageFile;
}
