package com.foodorder.foodapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.foodorder.foodapp.enums.ESuggestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "suggestions")
public class Suggestion extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  @Column(length = 150)
  private String title;

  @Column(length = 1000)
  private String description;

  @Column(length = 255)
  private String image;

  @Enumerated(EnumType.STRING)
  private ESuggestion status;
}
