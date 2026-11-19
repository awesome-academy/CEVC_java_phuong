package com.foodorder.foodapp.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport.OrderStatusCount;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport.TopProduct;
import com.foodorder.foodapp.repository.OrderRepository;

import lombok.AllArgsConstructor;

/**
 * Order statistics by month. Used by the monthly report email (#18); meant to be reused by the
 * admin statistics page so both show the same numbers.
 */
@Service
@AllArgsConstructor
public class OrderStatisticsService {
  static final int TOP_PRODUCTS = 5;
  private static final String COMPLETED = "COMPLETED";

  private final OrderRepository orderRepository;

  /**
   * Orders created in {@code month}. orders.created_at is a local date-time written with the
   * server time zone, so the month boundaries are local date-times too.
   */
  @Transactional(readOnly = true)
  public MonthlyOrderReport monthlyReport(YearMonth month) {
    LocalDateTime from = month.atDay(1).atStartOfDay();
    LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();

    List<OrderStatusCount> byStatus = orderRepository.countByStatusCreatedBetween(from, to);
    List<TopProduct> topProducts = orderRepository.findTopProductsCreatedBetween(from, to,
        PageRequest.of(0, TOP_PRODUCTS));

    long totalOrders = byStatus.stream().mapToLong(OrderStatusCount::orders).sum();
    long revenue = byStatus.stream()
        .filter(status -> COMPLETED.equals(status.status()))
        .mapToLong(OrderStatusCount::amount)
        .sum();

    return new MonthlyOrderReport(month, totalOrders, revenue, byStatus, topProducts);
  }
}
