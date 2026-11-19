package com.foodorder.foodapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Async: order notifications are sent on Spring Boot's task executor, so placing an order never waits for Chatwork or
 *         SMTP. @Scheduled: the monthly order report.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncSchedulingConfig {
}
