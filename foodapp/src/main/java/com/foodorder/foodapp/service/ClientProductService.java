package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.PageRequest;

import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.specification.ProductSpecification;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.product.DetailProductDTO;
import com.foodorder.foodapp.dto.product.ClientDetailProductDTO;
import com.foodorder.foodapp.dto.product.ClientProductDTO;
import com.foodorder.foodapp.dto.product.SearchProductDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Product;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class ClientProductService {
  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;

  public Page<ClientProductDTO> getPublicProducts(SearchProductDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(), Sort.by("id").descending());

    Specification<Product> spec = Stream.of(
        ProductSpecification.withFetch(),
        ProductSpecification.hasName(params.getName()),
        ProductSpecification.hasCategory(params.getCategoryId()),
        ProductSpecification.hasProductType(params.getProductTypeId()),
        ProductSpecification.notDeleted(),
        ProductSpecification.isActive()).filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
    Page<Product> pageResult = productRepository.findAll(spec, pageable);

    return pageResult.map(product -> modelMapper.map(product, ClientProductDTO.class));
  }

  public ClientDetailProductDTO getProductById(@NonNull Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    return modelMapper.map(product, ClientDetailProductDTO.class);
  }
}
