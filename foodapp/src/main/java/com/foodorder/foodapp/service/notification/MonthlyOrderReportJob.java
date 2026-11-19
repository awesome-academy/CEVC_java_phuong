package com.foodorder.foodapp.service.notification;

import java.time.Clock;
import java.time.YearMonth;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.foodorder.foodapp.config.properties.NotificationProperties;
import com.foodorder.foodapp.service.OrderStatisticsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Emails the admins the order statistics of the previous month (#18). Runs at the start of a
 * month rather than on its last evening, so the report covers every order of the month.
 */
@Slf4j
@Component
public class MonthlyOrderReportJob {
  private final OrderStatisticsService orderStatisticsService;
  private final AdminMailService adminMailService;
  private final Clock clock;

  @Autowired
  public MonthlyOrderReportJob(OrderStatisticsService orderStatisticsService,
      AdminMailService adminMailService, NotificationProperties properties) {
    this(orderStatisticsService, adminMailService, Clock.system(ZoneId.of(properties.zone())));
  }

  /** Clock injectable so tests can pick "now". */
  MonthlyOrderReportJob(OrderStatisticsService orderStatisticsService,
      AdminMailService adminMailService, Clock clock) {
    this.orderStatisticsService = orderStatisticsService;
    this.adminMailService = adminMailService;
    this.clock = clock;
  }

  @Scheduled(cron = "${app.notification.monthly-report-cron}", zone = "${app.notification.zone}")
  public void sendPreviousMonthReport() {
    YearMonth month = YearMonth.now(clock).minusMonths(1);
    try {
      adminMailService.sendMonthlyReport(orderStatisticsService.monthlyReport(month));
      log.info("Monthly order report {} sent", month);
    } catch (RuntimeException e) {
      log.error("Monthly order report {} failed", month, e);
    }
  }
}
