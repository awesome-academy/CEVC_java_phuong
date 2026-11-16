package com.foodorder.foodapp.dto.suggestion;

import org.springframework.web.multipart.MultipartFile;

import com.foodorder.foodapp.validation.FileSize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSuggestionDTO {
  @NotBlank(message = "{validation.suggestion.title.required}")
  @Size(max = 150, message = "{validation.suggestion.title.max}")
  private String title;

  @NotBlank(message = "{validation.suggestion.description.required}")
  @Size(max = 1000, message = "{validation.suggestion.description.max}")
  private String description;

  @FileSize(max = 2 * 1024 * 1024, message = "{validation.image.size}")
  private MultipartFile imageFile;
}
