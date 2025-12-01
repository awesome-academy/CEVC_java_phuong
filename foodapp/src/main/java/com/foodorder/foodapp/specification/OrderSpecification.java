package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Order;

import jakarta.persistence.criteria.JoinType;

public class OrderSpecification {
  public static Specification<Order> hasProductName(String productName) {
    return (root, query, cb) -> {
      if (productName == null || productName.isBlank()) {
        return null;
      }

      if (query.getResultType() != Long.class) {
        query.distinct(true);
      }
      // Join Order -> OrderItem
      var orderItemsJoin = root.join("orderItems", JoinType.LEFT);

      // Join OrderItem -> Product
      var productJoin = orderItemsJoin.join("product", JoinType.LEFT);

      return cb.like(
          cb.lower(productJoin.get("name")),
          "%" + productName.toLowerCase() + "%");
    };
  }

  public static Specification<Order> withUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return null;
      }
      return cb.equal(root.get("user").get("id"), userId);
    };
  }

  public static Specification<Order> hasOrderStatus(Long orderStatusId) {
    return (root, query, cb) -> {
      if (orderStatusId == null) {
        return null;
      }
      return cb.equal(root.get("orderStatus").get("id"), orderStatusId);
    };
  }

  public static Specification<Order> hasPriceLevel(Long priceLevel) {
    return (root, query, cb) -> {
      if (priceLevel == null) {
        return null;
      }

      switch (priceLevel.intValue()) {
        case 1:
          return cb.lessThan(root.get("totalPrice"), 100_000);
        case 2:
          return cb.between(root.get("totalPrice"), 100_000, 1_000_000);
        case 3:
          return cb.greaterThan(root.get("totalPrice"), 1_000_000);
        default:
          return null;
      }
    };
  }

  public static Specification<Order> withFetch() {
    return (root, query, cb) -> {
      if (Order.class.equals(query.getResultType())) {
        root.fetch("orderItems", JoinType.LEFT)
            .fetch("product", JoinType.LEFT);
        root.fetch("orderStatus", JoinType.LEFT);
        root.fetch("user", JoinType.LEFT);
        root.fetch("orderAddress", JoinType.LEFT);
        query.distinct(true);
      } else {
        root.join("orderItems", JoinType.LEFT)
            .join("product", JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
