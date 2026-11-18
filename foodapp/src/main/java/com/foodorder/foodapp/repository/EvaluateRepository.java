package com.foodorder.foodapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Evaluate;

@Repository
public interface EvaluateRepository extends JpaRepository<Evaluate, Long>, JpaSpecificationExecutor<Evaluate> {
  Optional<Evaluate> findByProductIdAndUserId(Long productId, Long userId);

  @Query("SELECT COALESCE(AVG(e.rating), 0) FROM Evaluate e WHERE e.product.id = :productId AND e.isShow = true")
  double averageVisibleRating(@Param("productId") Long productId);

  long countByProductIdAndIsShowTrue(Long productId);
}
