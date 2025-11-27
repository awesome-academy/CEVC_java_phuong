package com.foodorder.foodapp.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReasonCancelDTO {
  @NotBlank(message = "{validation.order.reason.required}")
  @Size(max = 1000, message = "{validation.order.reason.max}")
  private String reason;
}
