package com.foodorder.foodapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.model.Order;

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
}
