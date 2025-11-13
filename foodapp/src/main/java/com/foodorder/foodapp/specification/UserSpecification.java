package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.User;

import jakarta.persistence.criteria.JoinType;

public class UserSpecification {
  public static Specification<User> hasFullName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank())
        return null;
      return cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<User> hasRole(Long roleId) {
    return (root, query, cb) -> {
      if (roleId == null)
        return null;
      return cb.equal(root.get("role").get("id"), roleId);
    };
  }

  public static Specification<User> notDeleted() {
    return (root, query, cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<User> withFetch() {
    return (root, query, cb) -> {
      if (Long.class != query.getResultType()) {
        root.fetch("role", JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
