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

  public static Specification<Order> withFetch() {
    return (root, query, cb) -> {
      // Avoid duplicate results with fetch joins
      root.fetch("orderItems", JoinType.LEFT)
          .fetch("product", JoinType.LEFT);
      root.fetch("orderStatus", JoinType.LEFT);

      // To avoid duplicate results
      query.distinct(true);

      return null; // Only fetch, no filtering
    };
  }
}
