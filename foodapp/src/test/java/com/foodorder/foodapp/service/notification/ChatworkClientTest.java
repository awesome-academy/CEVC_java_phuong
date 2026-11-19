package com.foodorder.foodapp.service.notification;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.foodorder.foodapp.config.properties.NotificationProperties;

/** The HTTP request ChatworkClient sends, checked against a mock server (no network). */
class ChatworkClientTest {
  private static final String BASE_URL = "https://api.chatwork.com/v2";

  @Test
  void sendMessage_postsFormToRoomWithToken() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    ChatworkClient client = new ChatworkClient(builder, properties("token-123", "42"));

    server.expect(requestTo(BASE_URL + "/rooms/42/messages"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("X-ChatWorkToken", "token-123"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(content().formDataContains(java.util.Map.of("body", "New order #1")))
        .andRespond(withSuccess("{\"message_id\":\"1\"}", MediaType.APPLICATION_JSON));

    client.sendMessage("New order #1");

    server.verify();
  }

  @Test
  void sendMessage_notConfigured_sendsNothing() {
    RestClient.Builder builder = RestClient.builder();
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    ChatworkClient client = new ChatworkClient(builder, properties("", ""));

    client.sendMessage("New order #1");

    server.verify(); // no request expected, none made
  }

  private static NotificationProperties properties(String token, String roomId) {
    return new NotificationProperties(new NotificationProperties.Chatwork(BASE_URL, token, roomId),
        new NotificationProperties.Mail(false, "foodapp@localhost"), "Asia/Ho_Chi_Minh");
  }
}
