package com.foodorder.foodapp.dto.product;

import java.time.LocalDateTime;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ListProductDTO {
  private Long id;

  private CategoryDTO category;

  private ProductTypeDTO productType;

  private String name;

  private Integer priority;

  @JsonIgnore
  private Integer price;

  private Integer quantity;

  private String image;

  private Float averageRating;

  private Boolean isActive;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  public String getPrice() {
    if (price == null)
      return "0";
    return java.text.NumberFormat.getInstance(Locale.US).format(price);
  }
}
