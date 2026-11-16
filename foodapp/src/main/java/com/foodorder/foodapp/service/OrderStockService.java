package com.foodorder.foodapp.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderItem;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.repository.ProductRepository;

import lombok.AllArgsConstructor;

/**
 * Stock changes shared by admin and client order flows.
 */
@Service
@AllArgsConstructor
public class OrderStockService {

  private final ProductRepository productRepository;

  /**
   * Adds the ordered quantities back to stock. Call only when an order is cancelled. Items are locked in productId
   * order so concurrent orders never wait on each other in a cycle.
   */
  public void restoreStock(Order order) {
    List<OrderItem> sortedItems = order.getOrderItems().stream()
        .sorted(Comparator.comparing(item -> item.getProduct().getId()))
        .toList();

    for (OrderItem item : sortedItems) {
      // findByIdForUpdate (no isActive/deletedAt filter): a deleted product still gets its stock back
      Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
          .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

      product.setQuantity(product.getQuantity() + item.getQuantity());
    }
  }
}
