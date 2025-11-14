package com.foodorder.foodapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserDTO {
  private String name;

  private Long roleId;

  private int page;

  private int perPage;
}
