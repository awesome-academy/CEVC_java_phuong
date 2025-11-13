package com.foodorder.foodapp.enums;

public enum MessageCode {
  CREATE_SUCCESS("toast.create.success"),
  CREATE_FAILED("toast.create.failed"),
  UPDATE_SUCCESS("toast.update.success"),
  UPDATE_FAILED("toast.update.failed"),
  DELETE_SUCCESS("toast.delete.success"),
  DELETE_FAILED("toast.delete.failed");

  private final String code;

  MessageCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
