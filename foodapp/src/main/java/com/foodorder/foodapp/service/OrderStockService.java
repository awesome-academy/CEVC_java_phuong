package com.foodorder.foodapp.service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.model.CartItem;
import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderItem;
import com.foodorder.foodapp.model.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;

/**
 * Stock changes shared by admin and client order flows (and the admin restock action). Both directions lock products the same way:
 * in productId order (concurrent orders wait instead of deadlocking) and re-read under the lock.
 */
@Service
@AllArgsConstructor
public class OrderStockService {

  private final EntityManager entityManager;

  /**
   * Takes the cart quantities out of stock when an order is placed.
   * Fails if a product is no longer for sale or has not enough stock (the whole order rolls back).
   */
  public void deductStock(List<CartItem> cartItems) {
    for (CartItem item : sortByProductId(cartItems, CartItem::getProduct)) {
      Product product = lockAndRefresh(item.getProduct());

      // Product may be hidden/deleted after it was added to the cart
      if (!Boolean.TRUE.equals(product.getIsActive()) || product.getDeletedAt() != null) {
        throw new BadRequestException("product.not.available", product.getName());
      }
      if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
        throw new BadRequestException("validation.product.quantity.insufficient");
      }

      product.setQuantity(product.getQuantity() - item.getQuantity());
    }
  }

  /**
   * Adds the ordered quantities back to stock. Call only when an order is cancelled.
   * Deleted/hidden products still get their stock back.
   */
  public void restoreStock(Order order) {
    for (OrderItem item : sortByProductId(order.getOrderItems(), OrderItem::getProduct)) {
      Product product = lockAndRefresh(item.getProduct());

      product.setQuantity(product.getQuantity() + item.getQuantity());
    }
  }

  /**
   * Adds stock from the admin "restock" action. Uses the same lock-then-reread as orders, so the
   * amount is added to the current stock instead of to a value read before a concurrent order.
   */
  public void addStock(Product product, int amount) {
    Product locked = lockAndRefresh(product);
    locked.setQuantity(locked.getQuantity() + amount);
  }

  /**
   * Lock the product row first, then re-read it, so the stock is the value committed by whoever held the lock.
   * Products are already loaded (EAGER from cart/order items): a locking query would return the cached instance
   * with the stock read before waiting. refresh(product, PESSIMISTIC_WRITE) is not enough either: the product
   * query joins category/product type, so Hibernate reads first and locks afterwards (follow-on locking).
   */
  private Product lockAndRefresh(Product product) {
    entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE); // SELECT id FROM products ... FOR UPDATE
    entityManager.refresh(product);
    return product;
  }

  private <T> List<T> sortByProductId(List<T> items, Function<T, Product> productOf) {
    return items.stream()
        .sorted(Comparator.comparing(item -> productOf.apply(item).getId()))
        .toList();
  }
}
