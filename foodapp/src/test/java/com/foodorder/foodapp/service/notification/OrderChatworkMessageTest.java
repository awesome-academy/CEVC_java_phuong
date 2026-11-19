package com.foodorder.foodapp.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderChatworkMessageTest {

  @Test
  void of_listsCustomerItemsTotalAndAddressInAnInfoBox() {
    OrderPlacedSummary order = summary("Giao trước 12h");

    assertThat(OrderChatworkMessage.of(order)).isEqualTo("""
        [info][title]New order #15[/title]Customer: Nguyễn Văn A (a@example.com)
        Placed at: 19/11/2026 17:30
        Items:
        - Phở bò x2 = 90.000 ₫
        - Trà đá x1 = 5.000 ₫
        Total: 95.000 ₫
        Deliver to: Nguyễn Văn A - 0901234567 - 1 Lê Lợi, Quận 1, Hồ Chí Minh
        Note: Giao trước 12h[/info]""");
  }

  @Test
  void of_chatworkTagsTypedByUser_areNotParsedAsTags() {
    String message = OrderChatworkMessage.of(summary("[toall] gấp [/info][To:123]"));

    assertThat(message).contains("Note: ［toall］ gấp ［/info］［To:123］[/info]")
        .doesNotContain("[toall]", "[To:123]");
  }

  @Test
  void of_withoutNote_omitsNoteLine() {
    assertThat(OrderChatworkMessage.of(summary("  "))).doesNotContain("Note:");
  }

  private static OrderPlacedSummary summary(String note) {
    return new OrderPlacedSummary(15L, "Nguyễn Văn A", "a@example.com",
        LocalDateTime.of(2026, 11, 19, 17, 30), 95_000, note, "Nguyễn Văn A", "0901234567",
        "1 Lê Lợi, Quận 1, Hồ Chí Minh",
        List.of(new OrderPlacedSummary.Line("Phở bò", 2, 45_000),
            new OrderPlacedSummary.Line("Trà đá", 1, 5_000)));
  }
}
