package com.foodorder.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.OrderAddress;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
}
