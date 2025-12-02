package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.model.Order;

import jakarta.persistence.criteria.JoinType;

public class EvaluateSpecification {
  public static Specification<Evaluate> hasProductName(String productName) {
    return (root, query, cb) -> {
      if (productName == null || productName.isBlank()) {
        return null;
      }
      return cb.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%");
    };
  }

  public static Specification<Evaluate> isShow() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get("isShow"));
    };
  }

  public static Specification<Evaluate> hasRatingLevel(Long ratingLevel) {
    return (root, query, cb) -> {
      if (ratingLevel == null) {
        return null;
      }

      switch (ratingLevel.intValue()) {
        case 1:
          return cb.equal(root.get("rating"), 1);
        case 2:
          return cb.equal(root.get("rating"), 2);
        case 3:
          return cb.equal(root.get("rating"), 3);
        case 4:
          return cb.equal(root.get("rating"), 4);
        case 5:
          return cb.equal(root.get("rating"), 5);
        default:
          return null;
      }
    };
  }

  public static Specification<Evaluate> withFetch() {
    return (root, query, cb) -> {
      if (Order.class.equals(query.getResultType())) {
        root.fetch("product", JoinType.LEFT);
        root.fetch("user", JoinType.LEFT);
        query.distinct(true);
      } else {
        root.join("product", JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
