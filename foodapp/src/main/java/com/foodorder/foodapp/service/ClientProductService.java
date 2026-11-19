package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.specification.ProductSpecification;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.product.ClientDetailProductDTO;
import com.foodorder.foodapp.dto.product.ClientProductDTO;
import com.foodorder.foodapp.dto.product.ClientSearchProductDTO;
import com.foodorder.foodapp.enums.EProductSort;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Product;
import com.foodorder.foodapp.utils.PageableUtils;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ClientProductService {
  private final ProductRepository productRepository;
  private final EvaluateRepository evaluateRepository;
  private final ModelMapper modelMapper;

  public Page<ClientProductDTO> getPublicProducts(ClientSearchProductDTO params, EProductSort sortBy,
      Pageable pageable) {
    if (params.getMinPrice() != null && params.getMaxPrice() != null
        && params.getMinPrice() > params.getMaxPrice()) {
      throw new BadRequestException("validation.product.price.range");
    }

    EProductSort sort = sortBy != null ? sortBy : EProductSort.NEWEST;
    Pageable sortedPageable = PageableUtils.withFixedSort(pageable, sort.toSort());

    Specification<Product> spec = buildSpecification(params);
    Page<Product> pageResult = productRepository.findAll(spec, sortedPageable);

    return pageResult.map(product -> modelMapper.map(product, ClientProductDTO.class));
  }

  private Specification<Product> buildSpecification(ClientSearchProductDTO params) {
    return Stream.of(
        ProductSpecification.withFetch(),
        ProductSpecification.hasName(params.getName()),
        ProductSpecification.hasCategory(params.getCategoryId()),
        ProductSpecification.hasProductType(params.getProductTypeId()),
        ProductSpecification.hasMinPrice(params.getMinPrice()),
        ProductSpecification.hasMaxPrice(params.getMaxPrice()),
        ProductSpecification.hasMinRating(params.getMinRating()),
        ProductSpecification.notDeleted(),
        ProductSpecification.isActive())
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  public ClientDetailProductDTO getProductById(@NonNull Long id) {
    Product product = productRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    long evaluateCount = evaluateRepository.countByProductIdAndIsShowTrue(id);
    ClientDetailProductDTO productDTO = modelMapper.map(product, ClientDetailProductDTO.class);
    productDTO.setEvaluateCount(evaluateCount);

    return productDTO;
  }
}
