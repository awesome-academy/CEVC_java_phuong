package com.foodorder.foodapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_addresses")
public class OrderAddress extends BaseModel {

  @OneToOne
  @JoinColumn(name = "order_id", nullable = false)
  @JsonBackReference
  private Order order;

  @Column(name = "receiver_name", nullable = false, length = 100)
  private String receiverName;

  @Column(name = "phone_number", nullable = false, length = 20)
  private String phoneNumber;

  @Column(length = 100, nullable = false)
  private String city;

  @Column(length = 100, nullable = false)
  private String district;

  @Column(length = 255, nullable = false)
  private String streetAddress;
}
