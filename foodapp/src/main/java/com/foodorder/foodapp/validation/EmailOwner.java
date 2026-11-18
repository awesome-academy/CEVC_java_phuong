package com.foodorder.foodapp.validation;

/**
 * Object validated by {@link UniqueEmail}. Lets the validator stay independent of the DTO classes.
 */
public interface EmailOwner {
  String getEmail();

  /**
   * Id of the user being edited, who may keep their own email. Create/register forms return null,
   * so any existing user with the same email is a duplicate.
   */
  default Long getEditingUserId() {
    return null;
  }
}
