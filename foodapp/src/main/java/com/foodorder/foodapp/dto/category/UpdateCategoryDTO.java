package com.foodorder.foodapp.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UpdateCategoryDTO extends CreateCategoryDTO {
  @NotNull(message = "ID is required")
  private Long id;
}
