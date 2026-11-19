package com.foodorder.foodapp.service.notification;

import static com.foodorder.foodapp.utils.MoneyFormat.vnd;

import java.time.format.DateTimeFormatter;

/** Chatwork text of a new order, in an [info] box (Chatwork message markup). */
public final class OrderChatworkMessage {
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private OrderChatworkMessage() {
  }

  public static String of(OrderPlacedSummary order) {
    StringBuilder text = new StringBuilder()
        .append("[info][title]New order #").append(order.orderId()).append("[/title]")
        .append("Customer: ").append(plain(order.customerName()))
        .append(" (").append(plain(order.customerEmail())).append(")\n")
        .append("Placed at: ").append(order.createdAt().format(TIME)).append('\n')
        .append("Items:\n");

    for (OrderPlacedSummary.Line item : order.items()) {
      text.append("- ").append(plain(item.productName()))
          .append(" x").append(item.quantity())
          .append(" = ").append(vnd(item.amount())).append('\n');
    }

    text.append("Total: ").append(vnd(order.totalPrice())).append('\n')
        .append("Deliver to: ").append(plain(order.receiverName()))
        .append(" - ").append(plain(order.phoneNumber()))
        .append(" - ").append(plain(order.address()));

    if (order.note() != null && !order.note().isBlank()) {
      text.append("\nNote: ").append(plain(order.note()));
    }
    return text.append("[/info]").toString();
  }

  /**
   * Text typed by users (name, address, note) must not act as Chatwork tags: "[toall]" would
   * mention the whole room, "[/info]" would break the box. Full-width brackets read the same but
   * are not parsed as tags.
   */
  static String plain(String value) {
    return value == null ? "" : value.replace('[', '［').replace(']', '］');
  }
}
