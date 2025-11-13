package com.foodorder.foodapp.repository;

import com.foodorder.foodapp.model.AuthProvider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
}
