package com.foodorder.foodapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Order;

import jakarta.persistence.LockModeType;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
  @Query("""
        SELECT
            CASE WHEN count(o) > 0 THEN TRUE ELSE FALSE END
        FROM
            Order o
        JOIN
            o.orderItems oi
        WHERE
            oi.product.id = :productId
            AND o.orderStatus.id IN (1, 2, 3)
      """)
  boolean existsActiveOrderForProduct(@Param("productId") Long productId);

  @Query("""
        SELECT
            CASE WHEN count(o) > 0 THEN TRUE ELSE FALSE END
        FROM
            Order o
        WHERE
            o.user.id = :userId
            AND o.orderStatus.id IN (1, 2, 3)
      """)
  boolean existsActiveOrderForUser(@Param("userId") Long userId);

  Optional<Order> findByUserIdAndId(Long userId, Long id);

  // Row lock (SELECT ... FOR UPDATE): concurrent status changes/cancels on the same order run one after another
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT o FROM Order o WHERE o.id = :id")
  Optional<Order> findByIdForUpdate(@Param("id") Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT o FROM Order o WHERE o.id = :id AND o.user.id = :userId")
  Optional<Order> findByUserIdAndIdForUpdate(@Param("userId") Long userId, @Param("id") Long id);

  @Query("""
        SELECT
            CASE WHEN count(o) > 0 THEN TRUE ELSE FALSE END
        FROM
            Order o
        JOIN
            o.orderItems oi
        WHERE
            oi.product.id = :productId
            AND o.orderStatus.name = 'COMPLETED'
            AND o.user.id = :userId
      """)
  boolean existsOrderCompletedByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
