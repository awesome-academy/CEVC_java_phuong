package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.BaseModel_;
import com.foodorder.foodapp.model.OrderItem_;
import com.foodorder.foodapp.model.Order_;
import com.foodorder.foodapp.model.Product_;

import jakarta.persistence.criteria.JoinType;

public class OrderSpecification {
  public static Specification<Order> hasProductName(String productName) {
    return (root, query, cb) -> {
      if (productName == null || productName.isBlank()) {
        return null;
      }

      query.distinct(true);

      // Join Order -> OrderItem
      var orderItemsJoin = root.join(Order_.orderItems, JoinType.LEFT);

      // Join OrderItem -> Product
      var productJoin = orderItemsJoin.join(OrderItem_.product, JoinType.LEFT);

      return cb.like(
          cb.lower(productJoin.get(Product_.name)),
          "%" + productName.toLowerCase() + "%");
    };
  }

  public static Specification<Order> withUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return null;
      }
      return cb.equal(root.get(Order_.user).get(BaseModel_.id), userId);
    };
  }

  public static Specification<Order> hasOrderStatus(Long orderStatusId) {
    return (root, query, cb) -> {
      if (orderStatusId == null) {
        return null;
      }
      return cb.equal(root.get(Order_.orderStatus).get(BaseModel_.id), orderStatusId);
    };
  }

  public static Specification<Order> hasPriceLevel(Long priceLevel) {
    return (root, query, cb) -> {
      if (priceLevel == null) {
        return null;
      }

      switch (priceLevel.intValue()) {
        case 1:
          return cb.lessThan(root.get(Order_.totalPrice), 100_000);
        case 2:
          return cb.between(root.get(Order_.totalPrice), 100_000, 1_000_000);
        case 3:
          return cb.greaterThan(root.get(Order_.totalPrice), 1_000_000);
        default:
          return null;
      }
    };
  }

  public static Specification<Order> withFetch() {
    return (root, query, cb) -> {
      if (Order.class.equals(query.getResultType())) {
        root.fetch(Order_.orderStatus, JoinType.LEFT);
        root.fetch(Order_.user, JoinType.LEFT);
        root.fetch(Order_.orderAddress, JoinType.LEFT);
      }

      return null;
    };
  }
}
