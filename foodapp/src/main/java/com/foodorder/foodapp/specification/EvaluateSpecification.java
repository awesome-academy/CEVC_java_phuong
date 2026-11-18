package com.foodorder.foodapp.specification;

import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.model.BaseModel_;
import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.model.Evaluate_;

import jakarta.persistence.criteria.JoinType;

public class EvaluateSpecification {
  public static Specification<Evaluate> hasProduct(Long productId) {
    return (root, query, cb) -> {
      if (productId == null) {
        return null;
      }
      return cb.equal(root.get(Evaluate_.product).get(BaseModel_.id), productId);
    };
  }

  public static Specification<Evaluate> hasUser(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return null;
      }
      return cb.equal(root.get(Evaluate_.user).get(BaseModel_.id), userId);
    };
  }

  public static Specification<Evaluate> isShow() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get(Evaluate_.isShow));
    };
  }

  /** null = no filter (shown and hidden). */
  public static Specification<Evaluate> hasShowStatus(Boolean isShow) {
    return (root, query, cb) -> {
      if (isShow == null) {
        return null;
      }
      return cb.equal(root.get(Evaluate_.isShow), isShow);
    };
  }

  public static Specification<Evaluate> hasRatingLevel(Long ratingLevel) {
    return (root, query, cb) -> {
      if (ratingLevel == null) {
        return null;
      }

      int rating = ratingLevel.intValue();
      if (rating >= 1 && rating <= 5) {
        return cb.equal(root.get(Evaluate_.rating), rating);
      } else {
        return null;
      }
    };
  }

  public static Specification<Evaluate> withFetch() {
    return (root, query, cb) -> {
      if (Evaluate.class.equals(query.getResultType())) {
        root.fetch(Evaluate_.product, JoinType.LEFT);
        root.fetch(Evaluate_.user, JoinType.LEFT);
        query.distinct(true);
      } else {
        root.join(Evaluate_.product, JoinType.LEFT);
        query.distinct(true);
      }
      return null;
    };
  }
}
