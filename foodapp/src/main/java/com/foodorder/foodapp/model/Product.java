package com.foodorder.foodapp.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  @JsonBackReference
  private Category category;

  @ManyToOne
  @JoinColumn(name = "product_type_id", nullable = false)
  @JsonBackReference
  private ProductType productType;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false)
  private Integer price;

  @Column(nullable = false)
  private Integer quantity;

  @Column(length = 255)
  private String image;

  @Column(nullable = false)
  private Float average_rating;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
