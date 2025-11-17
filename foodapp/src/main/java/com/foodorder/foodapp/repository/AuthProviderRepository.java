package com.foodorder.foodapp.repository;

import com.foodorder.foodapp.model.AuthProvider;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
  Optional<AuthProvider> findByName(String name);
}
