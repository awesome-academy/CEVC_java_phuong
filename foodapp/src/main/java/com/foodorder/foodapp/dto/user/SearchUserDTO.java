package com.foodorder.foodapp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class SearchUserDTO {
  private String name;

  private Long roleId;

  public SearchUserDTO(String name, Long roleId) {
    this.name = name;
    this.roleId = roleId;
  }
}
