package com.foodorder.foodapp.dto.product;

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
public class ClientDetailProductDTO {
  private Long id;

  private CategoryDTO category;

  private ProductTypeDTO productType;

  private String name;

  private String description;

  private Integer priority;

  private Integer price;

  private Integer quantity;

  private String image;

  private Float averageRating;
}
