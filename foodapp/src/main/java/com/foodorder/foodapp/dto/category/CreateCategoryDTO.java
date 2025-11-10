package com.foodorder.foodapp.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDTO {
  @NotBlank(message = "{validation.category.name.required}")
  @Size(max = 100, message = "{validation.category.name.max}")
  private String name;

  @NotNull(message = "{validation.category.priority.required}")
  private Integer priority;

  private Long parentId;
}
