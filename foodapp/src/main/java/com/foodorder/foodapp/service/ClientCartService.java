package com.foodorder.foodapp.service;

import com.foodorder.foodapp.dto.cart.AddToCartDTO;
import com.foodorder.foodapp.dto.cart.DetailCartDTO;
import com.foodorder.foodapp.dto.cart.UpdateToCartDTO;
import com.foodorder.foodapp.enums.CartActionType;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Cart;
import com.foodorder.foodapp.model.CartItem;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.repository.CartRepository;
import com.foodorder.foodapp.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientCartService {

  private final CartRepository cartRepository;
  private final ModelMapper modelMapper;
  private final ProductRepository productRepository;

  public DetailCartDTO getCartByUserId(Long userId) {
    Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());
    return modelMapper.map(cart, DetailCartDTO.class);
  }

  @Transactional
  public DetailCartDTO addItemToCart(Long userId, AddToCartDTO addToCartDTO) {
    {
      Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());

      addOrUpdateCartItem(cart, addToCartDTO, false);

      cart = cartRepository.save(cart);
      return modelMapper.map(cart, DetailCartDTO.class);
    }
  }

  @Transactional
  public DetailCartDTO updateCart(Long userId, UpdateToCartDTO updateToCartDTO) {
    Cart cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("cart.not.found"));

    switch (updateToCartDTO.getActionType()) {
      case CartActionType.UPDATE:
        updateCartItem(cart, updateToCartDTO);
        break;

      case CartActionType.REMOVE_ITEM:
        deleteCartItem(cart, updateToCartDTO);
        break;

      case CartActionType.REMOVE_ALL:
        deleteAllCartItems(cart);
        break;

      default:
        break;
    }

    cart = cartRepository.save(cart);
    return modelMapper.map(cart, DetailCartDTO.class);
  }

  private void deleteAllCartItems(Cart cart) {
    cart.getCartItems().clear();
  }

  private void deleteCartItem(Cart cart, UpdateToCartDTO updateToCartDTO) {
    for (AddToCartDTO dto : updateToCartDTO.getItems()) {
      cart.getCartItems().removeIf(i -> i.getProduct().getId().equals(dto.getProductId()));
    }
  }

  private void updateCartItem(Cart cart, UpdateToCartDTO updateToCartDTO) {
    for (AddToCartDTO updateDTO : updateToCartDTO.getItems()) {
      addOrUpdateCartItem(cart, updateDTO, true);
    }
  }

  private void addOrUpdateCartItem(
      Cart cart,
      AddToCartDTO addToCartDTO,
      boolean replaceQuantity) {
    Product product = productRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(
        addToCartDTO.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    Optional<CartItem> existing = cart.getCartItems().stream()
        .filter(i -> i.getProduct().getId().equals(product.getId()))
        .findFirst();

    int newQuantity = replaceQuantity
        ? addToCartDTO.getQuantity()
        : existing.map(CartItem::getQuantity).orElse(0) + addToCartDTO.getQuantity();

    validateQuantity(product.getQuantity(), newQuantity);

    if (newQuantity <= 0) {
      existing.ifPresent(cart.getCartItems()::remove);
    } else {
      if (existing.isPresent()) {
        existing.get().setQuantity(newQuantity);
      } else {
        CartItem newItem = new CartItem();
        newItem.setProduct(product);
        newItem.setQuantity(newQuantity);
        newItem.setCart(cart);
        cart.getCartItems().add(newItem);
      }
    }
  }

  private void validateQuantity(Integer quantity, Integer cartQuantity) {
    if (quantity == null || quantity < cartQuantity) {
      throw new BadRequestException("validation.cart.quantity.max");
    }
  }
}
