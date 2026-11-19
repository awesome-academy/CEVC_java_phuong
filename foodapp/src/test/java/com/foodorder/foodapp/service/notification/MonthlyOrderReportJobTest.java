package com.foodorder.foodapp.service.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.service.OrderStatisticsService;

class MonthlyOrderReportJobTest {
  private static final ZoneId VIETNAM = ZoneId.of("Asia/Ho_Chi_Minh");

  private final OrderStatisticsService statistics = mock(OrderStatisticsService.class);
  private final AdminMailService mail = mock(AdminMailService.class);

  @Test
  void sendPreviousMonthReport_onFirstOfMonth_reportsPreviousMonth() {
    MonthlyOrderReport report = emptyReport(YearMonth.of(2026, 11));
    when(statistics.monthlyReport(YearMonth.of(2026, 11))).thenReturn(report);

    jobAt("2026-12-01T01:00:00Z").sendPreviousMonthReport(); // 08:00 in Vietnam

    verify(mail).sendMonthlyReport(report);
  }

  @Test
  void sendPreviousMonthReport_inJanuary_reportsDecemberOfPreviousYear() {
    // 2026-12-31T17:30Z is already 2027-01-01 00:30 in Vietnam
    jobAt("2026-12-31T17:30:00Z").sendPreviousMonthReport();

    verify(statistics).monthlyReport(YearMonth.of(2026, 12));
  }

  @Test
  void sendPreviousMonthReport_mailFails_doesNotThrow() {
    when(statistics.monthlyReport(any())).thenReturn(emptyReport(YearMonth.of(2026, 11)));
    doThrow(new IllegalStateException("SMTP down")).when(mail).sendMonthlyReport(any());

    jobAt("2026-12-01T01:00:00Z").sendPreviousMonthReport(); // logged, scheduler keeps running
  }

  private MonthlyOrderReportJob jobAt(String instant) {
    return new MonthlyOrderReportJob(statistics, mail, Clock.fixed(Instant.parse(instant), VIETNAM));
  }

  private static MonthlyOrderReport emptyReport(YearMonth month) {
    return new MonthlyOrderReport(month, 0, 0, List.of(), List.of());
  }
}
