package com.foodorder.foodapp.dto.user;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.foodorder.foodapp.dto.auth_provider.AuthProviderDTO;
import com.foodorder.foodapp.dto.role.RoleDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DetailUserDTO {
  private Long id;

  private AuthProviderDTO authProvider;

  private RoleDTO role;

  private String fullName;

  private String email;

  private Integer age;

  private String avatar;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
}
