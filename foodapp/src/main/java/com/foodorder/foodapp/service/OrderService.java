package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import com.foodorder.foodapp.repository.OrderRepository;
import com.foodorder.foodapp.repository.OrderStatusRepository;
import com.foodorder.foodapp.specification.OrderSpecification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.order.AdminDetailOrderDTO;
import com.foodorder.foodapp.dto.order.AdminSearchOrderDTO;
import com.foodorder.foodapp.dto.order.AdminUpdateOrderStatusDTO;
import com.foodorder.foodapp.dto.order.ListOrderDTO;
import com.foodorder.foodapp.dto.order_status.OrderStatusDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderStatus;
import com.foodorder.foodapp.utils.PageableUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class OrderService {
  private final ModelMapper modelMapper;
  private final OrderRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final OrderStockService orderStockService;

  public Page<ListOrderDTO> getAllOrders(AdminSearchOrderDTO params, Pageable pageable) {
    Pageable sortedPageable = PageableUtils.withFixedSort(pageable, Sort.by("id").descending());

    Specification<Order> spec = buildSpecification(params);

    Page<Order> pageResult = orderRepository.findAll(spec, sortedPageable);

    return pageResult.map(order -> modelMapper.map(order,
        ListOrderDTO.class));
  }

  private Specification<Order> buildSpecification(AdminSearchOrderDTO params) {
    return Stream.of(
        OrderSpecification.hasProductName(params.getProductName()),
        OrderSpecification.hasOrderStatus(params.getOrderStatusId()),
        OrderSpecification.hasPriceLevel(params.getPriceLevel()),
        OrderSpecification.withFetch())
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  public List<OrderStatusDTO> getAllOrderStatuses() {
    return orderStatusRepository.findAll().stream()
        .map(orderStatus -> modelMapper.map(orderStatus,
            OrderStatusDTO.class))
        .collect(Collectors.toList());
  }

  public AdminDetailOrderDTO getOrderById(@NonNull Long id) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("order.not.found"));

    return modelMapper.map(order, AdminDetailOrderDTO.class);
  }

  @Transactional
  public AdminDetailOrderDTO updateOrderStatus(Long id, AdminUpdateOrderStatusDTO updateOrderStatusDTO) {
    Order order = orderRepository.findByIdForUpdate(id)
        .orElseThrow(() -> new ResourceNotFoundException("order.not.found"));
    Set<String> nonUpdateStatuses = Set.of("COMPLETED", "CANCELLED");

    if (nonUpdateStatuses.contains(order.getOrderStatus().getName())) {
      throw new BadRequestException("order.update.not.allowed");
    }

    OrderStatus orderStatus = orderStatusRepository.findById(updateOrderStatusDTO.getOrderStatusId())
        .orElseThrow(() -> new ResourceNotFoundException("order.status.not.found"));

    order.setOrderStatus(orderStatus);

    // Stock is given back only when the order is cancelled (once: CANCELLED orders are blocked above).
    // Admin may cancel a SHIPPED order (e.g. failed delivery), unlike the customer, but must give a reason
    // so the customer knows why
    if ("CANCELLED".equals(orderStatus.getName())) {
      String reasonCancel = updateOrderStatusDTO.getReasonCancel();
      if (reasonCancel == null || reasonCancel.isBlank()) {
        throw new BadRequestException("validation.order.reason.required");
      }
      order.setReasonCancel(reasonCancel.trim());
      orderStockService.restoreStock(order);
    }

    order = orderRepository.save(order);

    return modelMapper.map(order, AdminDetailOrderDTO.class);
  }
}
