package com.foodorder.foodapp.dto.suggestion;

import com.foodorder.foodapp.enums.ESuggestion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateSuggestionStatusDTO {
  @NotNull(message = "{validation.suggestion.status.required}")
  private ESuggestion status;

  @Size(max = 1000, message = "{validation.suggestion.reason_reject.max}")
  private String reasonReject;
}
