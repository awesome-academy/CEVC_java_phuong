package com.foodorder.foodapp.model;

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
@Table(name = "users")
public class User extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  @JsonBackReference
  private Role role;

  @ManyToOne
  @JoinColumn(name = "auth_provider_id", nullable = false)
  @JsonBackReference
  private AuthProvider authProvider;

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(length = 255, nullable = false, unique = true)
  private String email;

  @Column(length = 255, nullable = false)
  private String password;

  @Column
  private Integer age;

  @Column(length = 255)
  private String avatar;
}
