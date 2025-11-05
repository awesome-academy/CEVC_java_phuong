package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;

import com.foodorder.foodapp.repository.CategoryRepository;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.category.CreateCategoryDTO;
import com.foodorder.foodapp.dto.category.DetailCategoryDTO;
import com.foodorder.foodapp.dto.category.ListCategoryDTO;
import com.foodorder.foodapp.dto.category.SearchCategoryDTO;
import com.foodorder.foodapp.dto.category.UpdateCategoryDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Category;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;

  public Page<ListCategoryDTO> getAllCategories(SearchCategoryDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(), Sort.by("id").descending());

    Page<Category> pageResult;
    if (params.getName() != null && !params.getName().isBlank()) {
      pageResult = categoryRepository.findByNameContainingIgnoreCase(params.getName(), pageable);
    } else {
      pageResult = categoryRepository.findAll(pageable);
    }
    return pageResult.map(category -> modelMapper.map(category, ListCategoryDTO.class));
  }

  public List<ListCategoryDTO> getCategorySelectOptions() {
    List<Category> categories = categoryRepository.findAll();
    return categories.stream()
        .map(category -> modelMapper.map(category, ListCategoryDTO.class))
        .collect(Collectors.toList());
  }

  public DetailCategoryDTO getCategoryById(@NonNull Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    return modelMapper.map(category, DetailCategoryDTO.class);
  }

  public DetailCategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {
    Long parentId = createCategoryDTO.getParentId();
    Category category = modelMapper.map(createCategoryDTO, Category.class);

    if (parentId != null) {
      Category parentCategory = categoryRepository.findById(parentId)
          .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + parentId));

      category.setParent(parentCategory);
    }
    category = categoryRepository.save(category);
    return modelMapper.map(category, DetailCategoryDTO.class);
  }

  public DetailCategoryDTO updateCategory(UpdateCategoryDTO updateCategoryDTO) {
    Long parentId = updateCategoryDTO.getParentId();
    Category category = modelMapper.map(updateCategoryDTO, Category.class);

    if (parentId != null) {
      Category parentCategory = categoryRepository.findById(parentId)
          .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + parentId));

      category.setParent(parentCategory);
    }
    category = categoryRepository.save(category);
    return modelMapper.map(category, DetailCategoryDTO.class);
  }

  public void deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    categoryRepository.delete(category);
  }
}
