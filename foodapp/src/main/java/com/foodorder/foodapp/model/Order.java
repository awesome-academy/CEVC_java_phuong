package com.foodorder.foodapp.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "order_status_id", nullable = false)
  private OrderStatus orderStatus;

  @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
  private List<OrderItem> items;

  @Column(name = "reason_cancel", length = 1000)
  private String reasonCancel;

  @Column(name = "total_price", nullable = false)
  private Integer totalPrice;
}
