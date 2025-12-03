package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Suggestion;

import jakarta.persistence.criteria.JoinType;

public class SuggestionSpecification {
  public static Specification<Suggestion> hasStatus(String status) {
    return (root, query, cb) -> {
      if (status == null || status.isBlank()) {
        return null;
      }
      return cb.equal(cb.lower(root.get("status")), status.toLowerCase());
    };
  }

  public static Specification<Suggestion> withFetch() {
    return (root, query, cb) -> {
      if (Suggestion.class.equals(query.getResultType())) {
        root.fetch("user", JoinType.LEFT);
      } else {
        root.join("user", JoinType.LEFT);
      }
      return null;
    };
  }
}
