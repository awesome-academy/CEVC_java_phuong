package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.evaluate.ClientDetailEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.CreateEvaluateDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.repository.OrderRepository;
import com.foodorder.foodapp.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@AllArgsConstructor
public class ClientEvaluateService {
  private final ModelMapper modelMapper;
  private final EvaluateRepository evaluateRepository;
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final ImageUploadService imageUploadService;

  public ClientDetailEvaluateDTO getEvaluate(User currentUser, Long productId) {
    Evaluate evaluates = evaluateRepository.findByProductIdAndUserIdAndIsShowTrue(productId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    return modelMapper.map(evaluates, ClientDetailEvaluateDTO.class);
  }

  @Transactional
  public ClientDetailEvaluateDTO createEvaluate(User currentUser, Long productId,
      CreateEvaluateDTO createEvaluateDTO) {
    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    Boolean existsOrderCompleted = orderRepository
        .existsOrderCompletedByUserIdAndProductId(currentUser.getId(), productId);

    if (!existsOrderCompleted) {
      throw new BadRequestException("evaluate.create.not.allowed");
    }

    Evaluate evaluate = evaluateRepository.findByProductIdAndUserIdAndIsShowTrue(productId, currentUser.getId())
        .orElse(new Evaluate());
    String newImagePath = null;
    String oldImagePath = evaluate.getImage();

    try {
      newImagePath = imageUploadService.uploadImage(createEvaluateDTO.getImageFile());

      evaluate.setProduct(product);
      evaluate.setUser(currentUser);
      evaluate.setContent(createEvaluateDTO.getContent());
      evaluate.setRating(createEvaluateDTO.getRating());
      evaluate.setIsShow(true);
      Optional.ofNullable(newImagePath).ifPresent(evaluate::setImage);

      evaluate = evaluateRepository.save(evaluate);
      imageUploadService.deleteImage(oldImagePath, newImagePath != null);

      Double avgRating = evaluateRepository
          .findByProductIdAndIsShowTrue(productId)
          .stream()
          .mapToInt(Evaluate::getRating)
          .average()
          .orElse(0.0);

      product.setAverageRating(avgRating.floatValue());
      productRepository.save(product);

      return modelMapper.map(evaluate, ClientDetailEvaluateDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(oldImagePath);
      throw e;
    }
  }

  @Transactional
  public void deleteEvaluate(User currentUser, Long productId) {
    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    Evaluate evaluate = evaluateRepository.findByProductIdAndUserIdAndIsShowTrue(productId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    evaluateRepository.delete(evaluate);

    Double avgRating = evaluateRepository
        .findByProductIdAndIsShowTrue(productId)
        .stream()
        .mapToInt(Evaluate::getRating)
        .average()
        .orElse(0.0);

    product.setAverageRating(avgRating.floatValue());
    productRepository.save(product);
  }
}
