package com.foodorder.foodapp.dto.cart;

import java.util.List;

import com.foodorder.foodapp.enums.CartActionType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToCartDTO {

  @NotNull(message = "{validation.cart.action_type.required}")
  private CartActionType actionType;

  private List<AddToCartDTO> items;
}
