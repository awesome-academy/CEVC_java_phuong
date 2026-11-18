package com.foodorder.foodapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.evaluate.ClientDetailEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.ClientEvaluateSearchDTO;
import com.foodorder.foodapp.dto.evaluate.ClientListEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.CreateEvaluateDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.repository.OrderRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.specification.EvaluateSpecification;
import com.foodorder.foodapp.utils.PageableUtils;

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
    Evaluate evaluate = evaluateRepository.findByProductIdAndUserIdAndIsShowTrue(productId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    return modelMapper.map(evaluate, ClientDetailEvaluateDTO.class);
  }

  public Page<ClientListEvaluateDTO> getEvaluates(ClientEvaluateSearchDTO params, Pageable pageable) {
    productRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(params.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    Pageable sortedPageable = PageableUtils.withFixedSort(pageable,
        Sort.by("createdAt").descending().and(Sort.by("id").descending()));

    Specification<Evaluate> spec = buildSpecification(params.getProductId());
    Page<Evaluate> pageResult = evaluateRepository.findAll(spec, sortedPageable);

    return pageResult.map(evaluate -> modelMapper.map(evaluate, ClientListEvaluateDTO.class));
  }

  @Transactional
  public ClientDetailEvaluateDTO createEvaluate(User currentUser, Long productId,
      CreateEvaluateDTO createEvaluateDTO) {
    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    boolean existsOrderCompleted = orderRepository
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

      caculateAvgRating(product);

      return modelMapper.map(evaluate, ClientDetailEvaluateDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newImagePath);
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

    caculateAvgRating(product);
  }

  private void caculateAvgRating(Product product) {
    double avgRating = evaluateRepository
        .findByProductIdAndIsShowTrue(
            product.getId())
        .stream()
        .mapToInt(Evaluate::getRating)
        .average()
        .orElse(0.0);

    product.setAverageRating((float) avgRating);
    productRepository.save(product);
  }

  private Specification<Evaluate> buildSpecification(Long productId) {
    return Stream.of(
        EvaluateSpecification.withFetch(),
        EvaluateSpecification.isShow(),
        EvaluateSpecification.hasProduct(productId))
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }
}
