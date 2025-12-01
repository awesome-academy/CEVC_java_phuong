package com.foodorder.foodapp.constants;

public interface RegexConstants {
  String PW_REGEX = "^[a-zA-Z0-9]{6,10}$";
  String PW_OPTIONAL_REGEX = "^$|[a-zA-Z0-9]{6,10}$";
  String PHONE_NUMBER_REGEX = "^[0-9]{9,11}$";
}
