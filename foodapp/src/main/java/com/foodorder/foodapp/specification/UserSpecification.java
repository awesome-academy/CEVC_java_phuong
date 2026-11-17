package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.model.BaseModel_;
import com.foodorder.foodapp.model.User_;

import jakarta.persistence.criteria.JoinType;

public class UserSpecification {
  public static Specification<User> hasFullName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank())
        return null;
      return cb.like(cb.lower(root.get(User_.fullName)), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<User> hasRole(Long roleId) {
    return (root, query, cb) -> {
      if (roleId == null)
        return null;
      return cb.equal(root.get(User_.role).get(BaseModel_.id), roleId);
    };
  }

  public static Specification<User> notDeleted() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(User_.deletedAt));
    };
  }

  public static Specification<User> withFetch() {
    return (root, query, cb) -> {
      if (Long.class != query.getResultType()) {
        root.fetch(User_.role, JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
