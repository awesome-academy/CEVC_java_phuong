package com.foodorder.foodapp.dto.product;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.foodorder.foodapp.dto.category.CategoryDTO;
import com.foodorder.foodapp.dto.product_type.ProductTypeDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailProductDTO {
  private Long id;

  private String name;

  private String description;

  private CategoryDTO category;

  private ProductTypeDTO productType;

  private Integer price;

  private Integer quantity;

  private String image;

  private Float averageRating;

  private Boolean isActive;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
}
