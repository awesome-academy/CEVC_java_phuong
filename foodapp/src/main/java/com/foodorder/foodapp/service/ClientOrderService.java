package com.foodorder.foodapp.service;

import com.foodorder.foodapp.dto.order.DetailOrderDTO;
import com.foodorder.foodapp.dto.order.ReasonCancelDTO;
import com.foodorder.foodapp.dto.order.SearchOrderDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Cart;
import com.foodorder.foodapp.model.CartItem;
import com.foodorder.foodapp.model.Order;
import com.foodorder.foodapp.model.OrderItem;
import com.foodorder.foodapp.model.OrderStatus;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.CartRepository;
import com.foodorder.foodapp.repository.OrderRepository;
import com.foodorder.foodapp.repository.OrderStatusRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.specification.OrderSpecification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

@Service
@AllArgsConstructor
public class ClientOrderService {

  private final CartRepository cartRepository;
  private final ModelMapper modelMapper;
  private final OrderRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final ProductRepository productRepository;

  public List<DetailOrderDTO> getAllOrders(User user, SearchOrderDTO params) {
    Specification<Order> spec = buildSpecification(params);
    List<Order> orders = orderRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));

    return orders.stream()
        .map(order -> modelMapper.map(order,
            DetailOrderDTO.class))
        .collect(Collectors.toList());
  }

  private Specification<Order> buildSpecification(SearchOrderDTO params) {
    return Stream.of(
        OrderSpecification.hasProductName(params.getProductName()))
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  public DetailOrderDTO getOrderById(User user, Long id) {
    Order order = orderRepository.findByUserIdAndId(user.getId(), id)
        .orElseThrow(() -> new ResourceNotFoundException("order.not.found"));
    return modelMapper.map(order, DetailOrderDTO.class);
  }

  @Transactional
  public DetailOrderDTO createOrder(User user) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("cart.not.found"));

    if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
      throw new BadRequestException("cart.empty");
    }

    Order order = new Order();
    order.setUser(user);

    // Build order items
    for (CartItem cartItem : cart.getCartItems()) {
      Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
          .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

      validateQuantity(product.getQuantity(), cartItem.getQuantity());

      OrderItem orderItem = new OrderItem();
      orderItem.setProduct(product);
      orderItem.setQuantity(cartItem.getQuantity());
      orderItem.setPrice(product.getPrice());
      orderItem.setOrder(order);

      order.getOrderItems().add(orderItem);
    }

    // Build order
    OrderStatus orderStatus = orderStatusRepository.findByName("PENDING").orElseThrow(
        () -> new ResourceNotFoundException("order_status.not.found"));
    order.setOrderStatus(orderStatus);

    order.setTotalPrice(order.getOrderItems().stream()
        .mapToInt(item -> item.getPrice() * item.getQuantity())
        .sum());
    order = orderRepository.save(order);

    // Update product quantity
    for (OrderItem orderItem : order.getOrderItems()) {
      Product product = orderItem.getProduct();
      product.setQuantity(product.getQuantity() - orderItem.getQuantity());
      productRepository.save(product);
    }

    // Clear cart
    cart.getCartItems().clear();
    cartRepository.save(cart);

    return modelMapper.map(order, DetailOrderDTO.class);
  }

  private void validateQuantity(Integer quantity, Integer cartQuantity) {
    if (quantity == null || quantity < cartQuantity) {
      throw new BadRequestException("validation.cart.quantity.max");
    }
  }

  @Transactional
  public DetailOrderDTO cancelOrder(Long orderId, User user, ReasonCancelDTO reasonCancelDTO) {
    Order order = orderRepository.findByUserIdAndId(user.getId(), orderId)
        .orElseThrow(() -> new ResourceNotFoundException("order.not.found"));

    Set<String> nonCancellableStatuses = Set.of("SHIPPED", "COMPLETED", "CANCELLED");

    if (nonCancellableStatuses.contains(order.getOrderStatus().getName())) {
      throw new BadRequestException("order.cancel.not.allowed");
    }

    OrderStatus cancelledStatus = orderStatusRepository.findByName("CANCELLED")
        .orElseThrow(() -> new ResourceNotFoundException("order_status.not.found"));

    order.setOrderStatus(cancelledStatus);
    order.setReasonCancel(reasonCancelDTO.getReason());
    orderRepository.save(order);

    // Optionally: restore product quantity
    for (OrderItem item : order.getOrderItems()) {
      Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
          .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

      product.setQuantity(product.getQuantity() + item.getQuantity());
      productRepository.save(product);
    }

    return modelMapper.map(order, DetailOrderDTO.class);
  }
}
