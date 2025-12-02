package com.foodorder.foodapp.dto.user_address;

import com.foodorder.foodapp.constants.RegexConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserAddressDTO {
  @NotBlank(message = "{validation.user_address.receiver_name.required}")
  @Size(max = 100, message = "{validation.user_address.receiver_name.max}")
  private String receiverName;

  @NotBlank(message = "{validation.user_address.phone_number.required}")
  @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "{validation.user_address.phone_number.invalid}")
  private String phoneNumber;

  @NotBlank(message = "{validation.user_address.city.required}")
  @Size(max = 100, message = "{validation.user_address.city.max}")
  private String city;

  @NotBlank(message = "{validation.user_address.district.required}")
  @Size(max = 100, message = "{validation.user_address.district.max}")
  private String district;

  @NotBlank(message = "{validation.user_address.street_address.required}")
  @Size(max = 255, message = "{validation.user_address.street_address.max}")
  private String streetAddress;
}
