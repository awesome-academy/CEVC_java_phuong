package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Product;

import jakarta.persistence.criteria.JoinType;

public class ProductSpecification {
  public static Specification<Product> hasName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank())
        return null;
      return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Product> hasCategory(Long categoryId) {
    return (root, query, cb) -> {
      if (categoryId == null)
        return null;
      return cb.equal(root.get("category").get("id"), categoryId);
    };
  }

  public static Specification<Product> hasProductType(Long productTypeId) {
    return (root, query, cb) -> {
      if (productTypeId == null)
        return null;
      return cb.equal(root.get("productType").get("id"), productTypeId);
    };
  }

  public static Specification<Product> notDeleted() {
    return (root, query, cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<Product> isActive() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get("isActive"));
    };
  }

  public static Specification<Product> withFetch() {
    return (root, query, cb) -> {
      if (Long.class != query.getResultType()) {
        root.fetch("category", JoinType.LEFT);
        root.fetch("productType", JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
