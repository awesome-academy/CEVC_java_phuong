package com.foodorder.foodapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport.OrderStatusCount;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport.TopProduct;
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

  /** Order count and total per status for orders created in [from, to). */
  @Query("""
        SELECT new com.foodorder.foodapp.dto.statistics.MonthlyOrderReport$OrderStatusCount(
            s.name, COUNT(o), COALESCE(SUM(o.totalPrice), 0L))
        FROM Order o JOIN o.orderStatus s
        WHERE o.createdAt >= :from AND o.createdAt < :to
        GROUP BY s.id, s.name
        ORDER BY s.id
      """)
  List<OrderStatusCount> countByStatusCreatedBetween(@Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  /** Products sold in orders created in [from, to), cancelled orders excluded, most units first. */
  @Query("""
        SELECT new com.foodorder.foodapp.dto.statistics.MonthlyOrderReport$TopProduct(
            p.id, p.name, SUM(oi.quantity), SUM(oi.quantity * oi.price))
        FROM OrderItem oi JOIN oi.order o JOIN oi.product p
        WHERE o.createdAt >= :from AND o.createdAt < :to
            AND o.orderStatus.name <> 'CANCELLED'
        GROUP BY p.id, p.name
        ORDER BY SUM(oi.quantity) DESC, p.id
      """)
  List<TopProduct> findTopProductsCreatedBetween(@Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to, Pageable pageable);
}
