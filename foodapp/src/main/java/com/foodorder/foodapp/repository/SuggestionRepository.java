package com.foodorder.foodapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long>, JpaSpecificationExecutor<Suggestion> {
  List<Suggestion> findByUserId(Long userId);

  Optional<Suggestion> findByIdAndUserId(Long id, Long userId);
}
