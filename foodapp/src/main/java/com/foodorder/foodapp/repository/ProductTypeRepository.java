package com.foodorder.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.ProductType;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {}
