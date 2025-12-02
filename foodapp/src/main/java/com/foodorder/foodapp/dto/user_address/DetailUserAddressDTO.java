package com.foodorder.foodapp.dto.user_address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailUserAddressDTO {
  private Long id;
  private String receiverName;
  private String phoneNumber;
  private String city;
  private String district;
  private String streetAddress;
}
