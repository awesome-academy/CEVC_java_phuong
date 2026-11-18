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
import com.foodorder.foodapp.dto.evaluate.ClientMyEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.ClientMyEvaluateSearchDTO;
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

  /** Returns the user's own evaluate even when it was hidden by an admin (isShow tells the client). */
  public ClientDetailEvaluateDTO getEvaluate(User currentUser, Long productId) {
    Evaluate evaluate = evaluateRepository.findByProductIdAndUserId(productId, currentUser.getId())
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

  /** The user's own evaluates on every product, hidden ones included. */
  public Page<ClientMyEvaluateDTO> getMyEvaluates(User currentUser, Long productId, Pageable pageable) {
    Pageable sortedPageable = PageableUtils.withFixedSort(pageable,
        Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    ClientMyEvaluateSearchDTO params = new ClientMyEvaluateSearchDTO(currentUser.getId(), productId);

    Specification<Evaluate> spec = Stream.of(
        EvaluateSpecification.withFetch(),
        EvaluateSpecification.hasUser(params.getUserId()),
        EvaluateSpecification.hasProduct(params.getProductId()))
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);

    return evaluateRepository.findAll(spec, sortedPageable)
        .map(evaluate -> modelMapper.map(evaluate, ClientMyEvaluateDTO.class));
  }

  @Transactional
  public ClientDetailEvaluateDTO createEvaluate(User currentUser, Long productId,
      CreateEvaluateDTO createEvaluateDTO) {
    // Lock the product: the evaluates table has no unique (product_id, user_id), so two
    // concurrent requests of the same user could otherwise both insert a new evaluate.
    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    if (!Boolean.TRUE.equals(product.getIsActive()) || product.getDeletedAt() != null) {
      throw new ResourceNotFoundException("product.not.found");
    }

    boolean existsOrderCompleted = orderRepository
        .existsOrderCompletedByUserIdAndProductId(currentUser.getId(), productId);

    if (!existsOrderCompleted) {
      throw new BadRequestException("evaluate.create.not.allowed");
    }

    // Look up hidden evaluates too, otherwise a hidden one is treated as missing and the
    // user could publish a new evaluate around the admin's moderation.
    Evaluate evaluate = evaluateRepository.findByProductIdAndUserId(productId, currentUser.getId())
        .orElse(new Evaluate());
    if (evaluate.getId() != null) {
      assertEditable(evaluate);
    }
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

    Evaluate evaluate = evaluateRepository.findByProductIdAndUserId(productId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));
    assertEditable(evaluate);

    evaluateRepository.delete(evaluate);

    caculateAvgRating(product);
  }

  /** A hidden evaluate is read-only for its owner: editing or deleting it would bypass moderation. */
  private void assertEditable(Evaluate evaluate) {
    if (!Boolean.TRUE.equals(evaluate.getIsShow())) {
      throw new BadRequestException("evaluate.hidden");
    }
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
