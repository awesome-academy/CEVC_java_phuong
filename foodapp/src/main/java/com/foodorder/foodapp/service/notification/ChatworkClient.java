package com.foodorder.foodapp.service.notification;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.foodorder.foodapp.config.properties.NotificationProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Posts messages to the configured Chatwork room (API v2: POST /rooms/{room_id}/messages).
 * Timeouts come from spring.http.client.* in application.yaml.
 */
@Slf4j
@Component
public class ChatworkClient {
  private final RestClient restClient;
  private final NotificationProperties.Chatwork chatwork;

  public ChatworkClient(RestClient.Builder restClientBuilder, NotificationProperties properties) {
    this.chatwork = properties.chatwork();
    this.restClient = restClientBuilder.baseUrl(chatwork.baseUrl()).build();
  }

  /** Sends {@code body} to the room; does nothing (logged) when Chatwork is not configured. */
  public void sendMessage(String body) {
    if (!chatwork.enabled()) {
      log.info("Chatwork not configured (CHATWORK_API_TOKEN, CHATWORK_ROOM_ID): message skipped");
      return;
    }

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("body", body);

    restClient.post()
        .uri("/rooms/{roomId}/messages", chatwork.roomId())
        .header("X-ChatWorkToken", chatwork.apiToken())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .toBodilessEntity();
  }
}
