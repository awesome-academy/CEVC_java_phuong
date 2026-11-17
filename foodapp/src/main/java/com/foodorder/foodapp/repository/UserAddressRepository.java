package com.foodorder.foodapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
  Page<UserAddress> findByUserId(Long userId, Pageable pageable);

  Optional<UserAddress> findByUserIdAndId(Long userId, Long id);
}
