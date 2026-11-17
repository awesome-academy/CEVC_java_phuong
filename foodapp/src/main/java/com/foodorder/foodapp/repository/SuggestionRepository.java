package com.foodorder.foodapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long>, JpaSpecificationExecutor<Suggestion> {
  Page<Suggestion> findByUserId(Long userId, Pageable pageable);

  Optional<Suggestion> findByIdAndUserId(Long id, Long userId);
}
