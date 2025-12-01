package com.foodorder.foodapp.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateOrderStatusDTO {
  @NotNull(message = "{validation.order.order_status_id.required}")
  private Long orderStatusId;

  @Size(max = 1000, message = "{validation.order.reason.max}")
  private String reasonCancel;
}
