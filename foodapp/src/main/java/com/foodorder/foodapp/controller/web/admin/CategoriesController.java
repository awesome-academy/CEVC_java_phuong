package com.foodorder.foodapp.controller.web.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.dto.category.CreateCategoryDTO;
import com.foodorder.foodapp.dto.category.DetailCategoryDTO;
import com.foodorder.foodapp.dto.category.ListCategoryDTO;
import com.foodorder.foodapp.dto.category.SearchCategoryDTO;
import com.foodorder.foodapp.dto.category.UpdateCategoryDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.service.CategoryService;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategoriesController {
  private final CategoryService categoryService;
  private final ModelMapper modelMapper;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model) {
    List<ListCategoryDTO> categories = categoryService.getCategorySelectOptions();
    model.addAttribute("categories", categories);
  }

  @GetMapping
  public String indexCategory(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "${app.pagination.default-page}") int page,
      @RequestParam(defaultValue = "${app.pagination.default-per-page}") int perPage,
      Model model) {
    SearchCategoryDTO params = new SearchCategoryDTO(name, page, perPage);
    Page<ListCategoryDTO> categories = categoryService.getAllCategories(params);
    model.addAttribute("search", params);
    model.addAttribute("categories", categories);
    model.addAttribute("totalPages", categories.getTotalPages());

    return "admin/categories/index";
  }

  @GetMapping("/{id}")
  public String detailCategory(@NonNull @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      DetailCategoryDTO category = categoryService.getCategoryById(id);
      model.addAttribute("category", category);

      return "admin/categories/detail";
    } catch (ResourceNotFoundException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/categories";
    }
  }

  @GetMapping("/new")
  public String newCategory(Model model) {
    model.addAttribute("category", new CreateCategoryDTO());

    return "admin/categories/new";
  }

  @PostMapping("/new")
  public String createCategory(
      @Valid @ModelAttribute("category") CreateCategoryDTO createCategoryDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("failedMessage", MessageCode.CREATE_FAILED.getCode());
      return "admin/categories/new";
    }
    categoryService.createCategory(createCategoryDTO);
    redirectAttributes.addFlashAttribute("successMessage", MessageCode.CREATE_SUCCESS.getCode());

    return "redirect:/admin/categories";
  }

  @GetMapping("/{id}/edit")
  public String editCategory(@NonNull @PathVariable Long id, Model model) {
    DetailCategoryDTO category = categoryService.getCategoryById(id);

    UpdateCategoryDTO updateCategoryDTO = modelMapper.map(category, UpdateCategoryDTO.class);
    if (category.getParent() != null) {
      updateCategoryDTO.setParentId(category.getParent().getId());
    }

    model.addAttribute("category", updateCategoryDTO);

    return "admin/categories/edit";
  }

  @PostMapping("/{id}/edit")
  public String updateCategory(
      @PathVariable Long id,
      @Valid @ModelAttribute("category") UpdateCategoryDTO updateCategoryDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("failedMessage", MessageCode.UPDATE_FAILED.getCode());
      return "admin/categories/edit";
    }

    categoryService.updateCategory(updateCategoryDTO);
    redirectAttributes.addFlashAttribute("successMessage", MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/categories";
  }

  @GetMapping("/{id}/delete")
  public String deleteCategory(
      @PathVariable Long id,
      RedirectAttributes redirectAttributes,
      Model model) {
    try {
      categoryService.deleteCategory(id);
      redirectAttributes.addFlashAttribute("successMessage", MessageCode.DELETE_SUCCESS.getCode());

      return "redirect:/admin/categories";
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/categories";
    }
  }
}
