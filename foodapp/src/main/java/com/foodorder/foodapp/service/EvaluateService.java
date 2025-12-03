package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;

import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.specification.EvaluateSpecification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.evaluate.AdminSearchEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.DetailEvaluateDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.model.Product;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class EvaluateService {
  private final ModelMapper modelMapper;
  private final ProductRepository productRepository;
  private final EvaluateRepository evaluateRepository;

  public Page<DetailEvaluateDTO> getAllEvaluates(AdminSearchEvaluateDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(),
        Sort.by("id").descending());

    Specification<Evaluate> spec = buildSpecification(params);

    Page<Evaluate> pageResult = evaluateRepository.findAll(spec, pageable);

    return pageResult.map(evaluate -> modelMapper.map(evaluate,
        DetailEvaluateDTO.class));
  }

  private Specification<Evaluate> buildSpecification(AdminSearchEvaluateDTO params) {
    return Stream.of(
        EvaluateSpecification.hasProductName(params.getProductName()),
        EvaluateSpecification.isShow(),
        EvaluateSpecification.hasRatingLevel(params.getRatingLevel()),
        EvaluateSpecification.withFetch())
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  @Transactional
  public void updateEvaluateDisplayStatus(Long id) {
    Evaluate evaluate = evaluateRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    Long productId = evaluate.getProduct().getId();

    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    evaluate.setIsShow(false);
    evaluateRepository.save(evaluate);

    double avgRating = evaluateRepository
        .findByProductIdAndIsShowTrue(productId)
        .stream()
        .mapToInt(Evaluate::getRating)
        .average()
        .orElse(0.0);

    product.setAverageRating((float) avgRating);
    productRepository.save(product);
  }
}
