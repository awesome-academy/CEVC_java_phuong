package com.foodorder.foodapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "categories")
public class Category extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "parent_id", nullable = true)
  @JsonBackReference
  private Category parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<Category> children;

  @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
  @JsonManagedReference
  private List<Product> products;

  @Column(nullable = false, length = 100)
  private String name;

  @Column
  private Integer priority;
}
