package com.foodorder.foodapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmailIgnoreCase(String email);

  Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

  /** Recipients of admin emails (order notifications, monthly report). */
  @Query("SELECT u.email FROM User u WHERE u.role.name = 'ADMIN' AND u.deletedAt IS NULL ORDER BY u.id")
  List<String> findActiveAdminEmails();
}
