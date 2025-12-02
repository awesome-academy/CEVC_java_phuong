package com.foodorder.foodapp.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateOrderDTO {
  @NotNull(message = "{validation.order.user_address.required}")
  private Long receiverAddressId;

  @Size(max = 1000, message = "{validation.order.note.max_length}")
  private String note;
}
