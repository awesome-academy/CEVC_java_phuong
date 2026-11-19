package com.foodorder.foodapp.dto.product;

import org.springframework.web.multipart.MultipartFile;

import com.foodorder.foodapp.validation.FileSize;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Fields shared by the admin create and edit product forms. Stock (quantity) is deliberately not
 * here: only the create form sets it; afterwards it changes through orders and the restock action.
 */
@Data
public abstract class ProductFormDTO {
  @NotBlank(message = "{validation.product.name.required}")
  @Size(max = 150, message = "{validation.product.name.max}")
  private String name;

  @Size(max = 1000, message = "{validation.product.description.max}")
  private String description;

  @NotNull(message = "{validation.product.price.required}")
  @Min(value = 0, message = "{validation.product.price.min}")
  private Integer price;

  @NotNull(message = "{validation.product.is_active.required}")
  private Boolean isActive = true;

  @NotNull(message = "{validation.product.category.required}")
  private Long categoryId;

  @NotNull(message = "{validation.product.product_type.required}")
  private Long productTypeId;

  @FileSize(max = 2 * 1024 * 1024, message = "{validation.image.size}")
  private MultipartFile imageFile;
}
