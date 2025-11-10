package com.foodorder.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Modifying
  @Query("UPDATE Category c SET c.parent = null WHERE c.parent.id = :parentId")
  void detachChildren(@Param("parentId") Long parentId);
}
