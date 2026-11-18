package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.Suggestion;
import com.foodorder.foodapp.model.Suggestion_;

import jakarta.persistence.criteria.JoinType;

public class SuggestionSpecification {
  public static Specification<Suggestion> hasStatus(String status) {
    return (root, query, cb) -> {
      if (status == null || status.isBlank()) {
        return null;
      }
      // status is an enum stored as text: compare as text, case-insensitive (unknown value -> no rows)
      return cb.equal(cb.lower(root.get(Suggestion_.status).as(String.class)), status.toLowerCase());
    };
  }

  public static Specification<Suggestion> withFetch() {
    return (root, query, cb) -> {
      if (Suggestion.class.equals(query.getResultType())) {
        root.fetch(Suggestion_.user, JoinType.LEFT);
      } else {
        root.join(Suggestion_.user, JoinType.LEFT);
      }
      return null;
    };
  }
}
