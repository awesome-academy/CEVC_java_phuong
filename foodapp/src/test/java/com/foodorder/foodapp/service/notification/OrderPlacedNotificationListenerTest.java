package com.foodorder.foodapp.service.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderAddress;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderPlacedNotificationListenerTest {
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private ChatworkClient chatworkClient;
  @Mock
  private AdminMailService adminMailService;
  /** Mock: TransactionTemplate just runs the callback (getTransaction returns no status). */
  @Mock
  private PlatformTransactionManager transactionManager;
  @InjectMocks
  private OrderPlacedNotificationListener listener;

  @Test
  void onOrderPlaced_chatworkFails_stillEmailsAdmins() {
    when(orderRepository.findById(7L)).thenReturn(Optional.of(order(7L)));
    doThrow(new IllegalStateException("Chatwork down")).when(chatworkClient).sendMessage(anyString());

    listener.onOrderPlaced(new OrderPlacedEvent(7L));

    verify(adminMailService).sendOrderPlaced(any());
  }

  @Test
  void onOrderPlaced_emailFails_doesNotThrow() {
    when(orderRepository.findById(7L)).thenReturn(Optional.of(order(7L)));
    doThrow(new IllegalStateException("SMTP down")).when(adminMailService).sendOrderPlaced(any());

    listener.onOrderPlaced(new OrderPlacedEvent(7L));

    verify(chatworkClient).sendMessage(anyString());
  }

  private static Order order(Long id) {
    User user = new User();
    user.setFullName("Nguyễn Văn A");
    user.setEmail("a@example.com");

    OrderAddress address = new OrderAddress();
    address.setReceiverName("Nguyễn Văn A");
    address.setPhoneNumber("0901234567");
    address.setStreetAddress("1 Lê Lợi");
    address.setDistrict("Quận 1");
    address.setCity("Hồ Chí Minh");

    Order order = new Order();
    order.setId(id);
    order.setUser(user);
    order.setOrderAddress(address);
    order.setTotalPrice(0);
    order.setCreatedAt(LocalDateTime.of(2026, 11, 19, 17, 30));
    order.setOrderItems(List.of());
    return order;
  }
}
