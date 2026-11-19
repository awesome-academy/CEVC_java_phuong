package com.foodorder.foodapp.service.notification;

/**
 * Published by ClientOrderService.createOrder. Listeners run after the order transaction commits,
 * so they only ever see orders that really exist.
 */
public record OrderPlacedEvent(Long orderId) {
}
