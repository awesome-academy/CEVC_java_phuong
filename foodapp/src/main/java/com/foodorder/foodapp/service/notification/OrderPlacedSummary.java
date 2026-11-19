package com.foodorder.foodapp.service.notification;

import java.time.LocalDateTime;
import java.util.List;

import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderAddress;

/**
 * What the order notifications show, copied out of the entities while the transaction is open so
 * Chatwork and email are sent without a database connection.
 */
public record OrderPlacedSummary(Long orderId, String customerName, String customerEmail,
    LocalDateTime createdAt, int totalPrice, String note, String receiverName, String phoneNumber,
    String address, List<Line> items) {

  /** One ordered product, at the price of the order. */
  public record Line(String productName, int quantity, int price) {
    public int amount() {
      return quantity * price;
    }
  }

  public static OrderPlacedSummary of(Order order) {
    OrderAddress address = order.getOrderAddress();
    List<Line> items = order.getOrderItems().stream()
        .map(item -> new Line(item.getProduct().getName(), item.getQuantity(), item.getPrice()))
        .toList();

    return new OrderPlacedSummary(order.getId(), order.getUser().getFullName(),
        order.getUser().getEmail(), order.getCreatedAt(), order.getTotalPrice(), order.getNote(),
        address.getReceiverName(), address.getPhoneNumber(),
        String.join(", ", address.getStreetAddress(), address.getDistrict(), address.getCity()),
        items);
  }
}
