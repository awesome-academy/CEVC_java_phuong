package com.foodorder.foodapp.dto.statistics;

import java.time.YearMonth;
import java.util.List;

/**
 * Orders placed in one month.
 *
 * @param totalOrders every order of the month, whatever its status
 * @param revenue     total price of the COMPLETED orders (money actually earned)
 * @param byStatus    order count and amount per status
 * @param topProducts best sellers by quantity, cancelled orders excluded
 */
public record MonthlyOrderReport(YearMonth month, long totalOrders, long revenue,
    List<OrderStatusCount> byStatus, List<TopProduct> topProducts) {

  /** Orders of one status in the month. */
  public record OrderStatusCount(String status, Long orders, Long amount) {
  }

  /** One product sold in the month: units and amount (order item price x quantity). */
  public record TopProduct(Long productId, String name, Long quantity, Long amount) {
  }
}
