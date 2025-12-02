package com.foodorder.foodapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Evaluate;

@Repository
public interface EvaluateRepository extends JpaRepository<Evaluate, Long>, JpaSpecificationExecutor<Evaluate> {
  Optional<Evaluate> findByProductIdAndUserIdAndIsShowTrue(Long productId, Long userId);

  List<Evaluate> findByProductIdAndIsShowTrue(Long productId);
}
