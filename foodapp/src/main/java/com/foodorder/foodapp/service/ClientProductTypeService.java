package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import com.foodorder.foodapp.repository.ProductTypeRepository;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.product_type.ProductTypeDTO;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientProductTypeService {
  private final ProductTypeRepository productTypeRepository;
  private final ModelMapper modelMapper;

  public List<ProductTypeDTO> getAllProductTypes() {
    return productTypeRepository.findAll().stream()
        .map(productType -> modelMapper.map(productType, ProductTypeDTO.class))
        .collect(Collectors.toList());
  }
}
