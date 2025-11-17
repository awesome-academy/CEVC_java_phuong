package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;

import com.foodorder.foodapp.repository.CategoryRepository;
import com.foodorder.foodapp.repository.OrderRepository;
import com.foodorder.foodapp.repository.ProductRepository;
import com.foodorder.foodapp.repository.ProductTypeRepository;
import com.foodorder.foodapp.specification.ProductSpecification;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.product.CreateProductDTO;
import com.foodorder.foodapp.dto.product.DetailProductDTO;
import com.foodorder.foodapp.dto.product.ListProductDTO;
import com.foodorder.foodapp.dto.product.SearchProductDTO;
import com.foodorder.foodapp.dto.product.UpdateProductDTO;
import com.foodorder.foodapp.dto.product_type.ProductTypeDTO;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Category;
import com.foodorder.foodapp.model.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class ProductService {
  private final ProductRepository productRepository;
  private final ProductTypeRepository productTypeRepository;
  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;
  private final OrderRepository orderRepository;
  private final ImageUploadService imageUploadService;

  public Page<ListProductDTO> getAllProducts(SearchProductDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(), Sort.by("id").descending());

    Specification<Product> spec = Stream.of(
        ProductSpecification.withFetch(),
        ProductSpecification.hasName(params.getName()),
        ProductSpecification.hasCategory(params.getCategoryId()),
        ProductSpecification.hasProductType(params.getProductTypeId()),
        ProductSpecification.notDeleted()).filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
    Page<Product> pageResult = productRepository.findAll(spec, pageable);

    return pageResult.map(product -> modelMapper.map(product, ListProductDTO.class));
  }

  public List<ProductTypeDTO> getAllProductTypes() {
    return productTypeRepository.findAll().stream()
        .map(productType -> modelMapper.map(productType, ProductTypeDTO.class))
        .collect(Collectors.toList());
  }

  public DetailProductDTO getProductById(@NonNull Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    return modelMapper.map(product, DetailProductDTO.class);
  }

  public DetailProductDTO createProduct(CreateProductDTO createProductDTO) {
    Long categoryId = createProductDTO.getCategoryId();
    Long productTypeId = createProductDTO.getProductTypeId();

    String imagePath = null;
    try {
      imagePath = imageUploadService.uploadImage(createProductDTO.getImageFile());
      Product product = new Product();
      product.setName(createProductDTO.getName());
      product.setDescription(createProductDTO.getDescription());
      product.setPrice(createProductDTO.getPrice());
      product.setQuantity(createProductDTO.getQuantity());
      product.setIsActive(createProductDTO.getIsActive());
      product.setImage(imagePath);

      if (categoryId != null) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));
        product.setCategory(category);
      }
      if (productTypeId != null) {
        var productType = productTypeRepository.findById(productTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("product_type.not.found"));
        product.setProductType(productType);
      }

      product = productRepository.save(product);
      return modelMapper.map(product, DetailProductDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(imagePath);
      throw e;
    }
  }

  public DetailProductDTO updateProduct(UpdateProductDTO updateProductDTO) {
    Long categoryId = updateProductDTO.getCategoryId();
    Long productTypeId = updateProductDTO.getProductTypeId();

    Product exitProduct = productRepository.findById(updateProductDTO.getId())
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    String oldImagePath = exitProduct.getImage();
    String newImagePath = null;
    try {
      newImagePath = imageUploadService.uploadImage(updateProductDTO.getImageFile());
      if (newImagePath != null) {
        exitProduct.setImage(newImagePath);
      }
      modelMapper.map(updateProductDTO, exitProduct);

      if (categoryId != null) {
        Category parentCategory = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("category.not.found"));

        exitProduct.setCategory(parentCategory);
      }
      if (productTypeId != null) {
        var productType = productTypeRepository.findById(productTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("product_type.not.found"));

        exitProduct.setProductType(productType);
      }

      exitProduct = productRepository.save(exitProduct);
      imageUploadService.deleteImage(oldImagePath, newImagePath != null);

      return modelMapper.map(exitProduct, DetailProductDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newImagePath);
      throw e;
    }
  }

  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

    boolean existsActiveOrderForProduct = orderRepository.existsActiveOrderForProduct(id);
    if (existsActiveOrderForProduct) {
      throw new BadRequestException("product.in.active.orders");
    }

    product.setDeletedAt(LocalDateTime.now());
    productRepository.save(product);
  }
}
