package com.foodorder.foodapp.controller.web.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.service.OrderStatisticsService;

class StatisticsControllerTest {
  private static final Clock NOV_2026 = Clock.fixed(Instant.parse("2026-11-19T10:00:00Z"), ZoneId.of("Asia/Ho_Chi_Minh"));

  private final OrderStatisticsService statistics = mock(OrderStatisticsService.class);
  private final StatisticsController controller = new StatisticsController(statistics, NOV_2026);

  @Test
  void statistics_withoutMonth_showsCurrentMonthWithoutNextLink() {
    MonthlyOrderReport report = emptyReport(YearMonth.of(2026, 11));
    when(statistics.monthlyReport(YearMonth.of(2026, 11))).thenReturn(report);
    ExtendedModelMap model = new ExtendedModelMap();

    String view = controller.statistics(null, model);

    assertThat(view).isEqualTo("admin/statistics/index");
    assertThat(model.get("report")).isSameAs(report);
    assertThat(model.get("previousMonth")).isEqualTo(YearMonth.of(2026, 10));
    assertThat(model.get("nextMonth")).isNull();
  }

  @Test
  void statistics_pastMonth_linksBothNeighbours() {
    when(statistics.monthlyReport(YearMonth.of(2026, 1))).thenReturn(emptyReport(YearMonth.of(2026, 1)));
    ExtendedModelMap model = new ExtendedModelMap();

    controller.statistics(YearMonth.of(2026, 1), model);

    assertThat(model.get("previousMonth")).isEqualTo(YearMonth.of(2025, 12));
    assertThat(model.get("nextMonth")).isEqualTo(YearMonth.of(2026, 2));
  }

  @Test
  void monthOptions_lastTwoYearsNewestFirst() {
    List<YearMonth> options = StatisticsController.monthOptions(YearMonth.of(2026, 11), YearMonth.of(2026, 10));

    assertThat(options).hasSize(StatisticsController.PICKER_MONTHS);
    assertThat(options.get(0)).isEqualTo(YearMonth.of(2026, 11));
    assertThat(options.get(options.size() - 1)).isEqualTo(YearMonth.of(2024, 12));
  }

  @Test
  void monthOptions_selectedMonthOutsideRange_isIncludedInOrder() {
    List<YearMonth> options = StatisticsController.monthOptions(YearMonth.of(2026, 11), YearMonth.of(2020, 3));

    assertThat(options).hasSize(StatisticsController.PICKER_MONTHS + 1)
        .endsWith(YearMonth.of(2024, 12), YearMonth.of(2020, 3));
  }

  private static MonthlyOrderReport emptyReport(YearMonth month) {
    return new MonthlyOrderReport(month, 0, 0, List.of(), List.of());
  }
}
