package com.foodorder.foodapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport.OrderStatusCount;
import com.foodorder.foodapp.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderStatisticsServiceTest {
  @Mock
  private OrderRepository orderRepository;
  @InjectMocks
  private OrderStatisticsService orderStatisticsService;

  @Test
  void monthlyReport_countsAllOrdersButRevenueOnlyFromCompleted() {
    LocalDateTime from = LocalDateTime.of(2026, 11, 1, 0, 0);
    LocalDateTime to = LocalDateTime.of(2026, 12, 1, 0, 0);
    when(orderRepository.countByStatusCreatedBetween(from, to)).thenReturn(List.of(
        new OrderStatusCount("PENDING", 2L, 100_000L),
        new OrderStatusCount("COMPLETED", 3L, 450_000L),
        new OrderStatusCount("CANCELLED", 1L, 70_000L)));
    when(orderRepository.findTopProductsCreatedBetween(eq(from), eq(to), any())).thenReturn(List.of());

    MonthlyOrderReport report = orderStatisticsService.monthlyReport(YearMonth.of(2026, 11));

    assertThat(report.totalOrders()).isEqualTo(6);
    assertThat(report.revenue()).isEqualTo(450_000);
    assertThat(report.byStatus()).hasSize(3);
  }
}
