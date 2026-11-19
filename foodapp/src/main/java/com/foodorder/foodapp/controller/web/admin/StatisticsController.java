package com.foodorder.foodapp.controller.web.admin;

import java.time.Clock;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodorder.foodapp.service.OrderStatisticsService;

/**
 * Order statistics by month (#19): order count, revenue, orders per status and top products.
 * {@code ?month=yyyy-MM}, current month by default; an invalid month shows the not found page.
 */
@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {
  static final int PICKER_MONTHS = 24;

  private final OrderStatisticsService orderStatisticsService;
  private final Clock clock;

  @Autowired
  public StatisticsController(OrderStatisticsService orderStatisticsService) {
    // System zone: orders.created_at is written with the server time zone too
    this(orderStatisticsService, Clock.systemDefaultZone());
  }

  /** Clock injectable so tests can pick "now". */
  StatisticsController(OrderStatisticsService orderStatisticsService, Clock clock) {
    this.orderStatisticsService = orderStatisticsService;
    this.clock = clock;
  }

  @GetMapping
  public String statistics(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
      Model model) {
    YearMonth currentMonth = YearMonth.now(clock);
    YearMonth selectedMonth = month != null ? month : currentMonth;

    model.addAttribute("report", orderStatisticsService.monthlyReport(selectedMonth));
    model.addAttribute("previousMonth", selectedMonth.minusMonths(1));
    // No link to months that have not started yet
    model.addAttribute("nextMonth", selectedMonth.isBefore(currentMonth) ? selectedMonth.plusMonths(1) : null);
    model.addAttribute("monthOptions", monthOptions(currentMonth, selectedMonth));

    return "admin/statistics/index";
  }

  /**
   * Months of the picker, newest first: the last {@value #PICKER_MONTHS} months, plus the selected
   * month when it is outside them (reached with the previous/next links or the URL).
   */
  static List<YearMonth> monthOptions(YearMonth currentMonth, YearMonth selectedMonth) {
    TreeSet<YearMonth> months = new TreeSet<>(Comparator.reverseOrder());
    for (int i = 0; i < PICKER_MONTHS; i++) {
      months.add(currentMonth.minusMonths(i));
    }
    months.add(selectedMonth);
    return List.copyOf(months);
  }
}
