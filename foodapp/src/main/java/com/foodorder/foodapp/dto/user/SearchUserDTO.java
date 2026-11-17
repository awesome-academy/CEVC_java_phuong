package com.foodorder.foodapp.dto.user;

import com.foodorder.foodapp.dto.pagination.PaginationDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class SearchUserDTO {
  private String name;

  private Long roleId;

  private PaginationDTO pagination;

  public SearchUserDTO(String name, Long roleId, PaginationDTO pagination) {
    this.name = name;
    this.roleId = roleId;
    this.pagination = pagination;
  }
}
