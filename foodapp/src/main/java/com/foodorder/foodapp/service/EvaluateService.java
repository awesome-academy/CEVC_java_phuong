package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.specification.EvaluateSpecification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.evaluate.AdminSearchEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.DetailEvaluateDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Evaluate;
import com.foodorder.foodapp.utils.PageableUtils;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class EvaluateService {
  private final ModelMapper modelMapper;
  private final ProductRatingService productRatingService;
  private final EvaluateRepository evaluateRepository;

  public Page<DetailEvaluateDTO> getAllEvaluates(AdminSearchEvaluateDTO params, Pageable pageable) {
    Pageable sortedPageable = PageableUtils.withFixedSort(pageable, Sort.by("id").descending());

    Specification<Evaluate> spec = buildSpecification(params);

    Page<Evaluate> pageResult = evaluateRepository.findAll(spec, sortedPageable);

    return pageResult.map(evaluate -> modelMapper.map(evaluate,
        DetailEvaluateDTO.class));
  }

  public DetailEvaluateDTO getEvaluateById(Long id) {
    Evaluate evaluate = evaluateRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    return modelMapper.map(evaluate, DetailEvaluateDTO.class);
  }

  private Specification<Evaluate> buildSpecification(AdminSearchEvaluateDTO params) {
    return Stream.of(
        EvaluateSpecification.hasProduct(params.getProductId()),
        EvaluateSpecification.hasShowStatus(params.getIsShow()),
        EvaluateSpecification.hasRatingLevel(params.getRatingLevel()),
        EvaluateSpecification.withFetch())
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  /** Flips show/hide of an evaluate and recalculates the product average rating. */
  @Transactional
  public void toggleDisplay(Long id) {
    Evaluate evaluate = evaluateRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("evaluate.not.found"));

    evaluate.setIsShow(!Boolean.TRUE.equals(evaluate.getIsShow()));
    evaluateRepository.save(evaluate);

    productRatingService.recalculate(evaluate.getProduct().getId());
  }
}
