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
      // Join Order -> OrderItem
      var orderItemsJoin = root.join("orderItems", JoinType.LEFT);

      // Join OrderItem -> Product
      var productJoin = orderItemsJoin.join("product", JoinType.LEFT);

      return cb.like(
          cb.lower(productJoin.get("name")),
          "%" + productName.toLowerCase() + "%");
    };
  }
}
