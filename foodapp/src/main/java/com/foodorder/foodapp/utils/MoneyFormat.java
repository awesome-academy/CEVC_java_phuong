package com.foodorder.foodapp.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * Formats VND amounts, e.g. 120000 -> "120.000 ₫". Static for Java code; also a bean named
 * "money" so Thymeleaf templates call it as {@code ${@money.vnd(order.totalPrice)}}.
 */
@Component("money")
public class MoneyFormat {
  private static final Locale VIETNAMESE = Locale.forLanguageTag("vi-VN");

  public static String vnd(long amount) {
    return NumberFormat.getIntegerInstance(VIETNAMESE).format(amount) + " ₫";
  }
}
