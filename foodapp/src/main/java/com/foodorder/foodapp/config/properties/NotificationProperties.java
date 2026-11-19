package com.foodorder.foodapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * app.notification.*: where order notifications and the monthly report are sent (#18).
 * Each channel is optional, so a local setup without Chatwork or SMTP still places orders.
 *
 * @param chatwork Chatwork room that receives a message per order
 * @param mail     emails to every active admin user
 * @param zone     time zone of the monthly report (month boundaries and the cron)
 */
@Validated
@ConfigurationProperties(prefix = "app.notification")
public record NotificationProperties(@Valid @NotNull Chatwork chatwork, @Valid @NotNull Mail mail,
    @NotBlank String zone) {

  /** Enabled when both the API token and the room id are set. */
  public record Chatwork(@NotBlank String baseUrl, String apiToken, String roomId) {
    public boolean enabled() {
      return apiToken != null && !apiToken.isBlank() && roomId != null && !roomId.isBlank();
    }
  }

  /** Sender address; SMTP itself is spring.mail.*. */
  public record Mail(boolean enabled, @NotBlank String from) {
  }
}
