package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.model.BaseModel_;
import com.foodorder.foodapp.model.Product_;

import jakarta.persistence.criteria.JoinType;

public class ProductSpecification {
  public static Specification<Product> hasName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank())
        return null;
      return cb.like(cb.lower(root.get(Product_.name)), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Product> hasCategory(Long categoryId) {
    return (root, query, cb) -> {
      if (categoryId == null)
        return null;
      return cb.equal(root.get(Product_.category).get(BaseModel_.id), categoryId);
    };
  }

  public static Specification<Product> hasProductType(Long productTypeId) {
    return (root, query, cb) -> {
      if (productTypeId == null)
        return null;
      return cb.equal(root.get(Product_.productType).get(BaseModel_.id), productTypeId);
    };
  }

  public static Specification<Product> notDeleted() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(Product_.deletedAt));
    };
  }

  public static Specification<Product> isActive() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get(Product_.isActive));
    };
  }

  public static Specification<Product> withFetch() {
    return (root, query, cb) -> {
      if (Long.class != query.getResultType()) {
        root.fetch(Product_.category, JoinType.LEFT);
        root.fetch(Product_.productType, JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
