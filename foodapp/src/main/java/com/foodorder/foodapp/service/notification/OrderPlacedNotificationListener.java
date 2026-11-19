package com.foodorder.foodapp.service.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Tells the team about a new order: a Chatwork message and an email to the admins (#18).
 *
 * <p>Runs after the order transaction commits (a rolled back order is never announced) and on
 * another thread (@Async), so placing an order neither waits for nor fails because of Chatwork
 * or SMTP. Each channel is tried on its own: one failing does not stop the other.
 */
@Slf4j
@Component
public class OrderPlacedNotificationListener {
  private final OrderRepository orderRepository;
  private final ChatworkClient chatworkClient;
  private final AdminMailService adminMailService;
  private final TransactionTemplate readOnlyTransaction;

  public OrderPlacedNotificationListener(OrderRepository orderRepository,
      ChatworkClient chatworkClient, AdminMailService adminMailService,
      PlatformTransactionManager transactionManager) {
    this.orderRepository = orderRepository;
    this.chatworkClient = chatworkClient;
    this.adminMailService = adminMailService;
    this.readOnlyTransaction = new TransactionTemplate(transactionManager);
    this.readOnlyTransaction.setReadOnly(true);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onOrderPlaced(OrderPlacedEvent event) {
    // Short read-only transaction just to copy the order (lazy items, address) into a summary;
    // the database connection is released before the slow HTTP / SMTP calls below
    OrderPlacedSummary order = readOnlyTransaction.execute(status ->
        orderRepository.findById(event.orderId())
            .map(OrderPlacedSummary::of)
            .orElseThrow(() -> new ResourceNotFoundException("order.not.found")));

    notify("Chatwork", order, () -> chatworkClient.sendMessage(OrderChatworkMessage.of(order)));
    notify("admin email", order, () -> adminMailService.sendOrderPlaced(order));
  }

  private void notify(String channel, OrderPlacedSummary order, Runnable send) {
    try {
      send.run();
    } catch (RuntimeException e) {
      log.error("Order #{}: {} notification failed", order.orderId(), channel, e);
    }
  }
}
