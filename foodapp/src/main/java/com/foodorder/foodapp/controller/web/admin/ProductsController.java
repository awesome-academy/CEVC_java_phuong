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

import com.foodorder.foodapp.dto.category.ListCategoryDTO;
import com.foodorder.foodapp.dto.product.CreateProductDTO;
import com.foodorder.foodapp.dto.product.DetailProductDTO;
import com.foodorder.foodapp.dto.product.ListProductDTO;
import com.foodorder.foodapp.dto.product.SearchProductDTO;
import com.foodorder.foodapp.dto.product.UpdateProductDTO;
import com.foodorder.foodapp.dto.product_type.ProductTypeDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.service.CategoryService;
import com.foodorder.foodapp.service.ProductService;

import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/products")
@AllArgsConstructor
public class ProductsController {
  private final ProductService productService;
  private final CategoryService categoryService;
  private final ModelMapper modelMapper;

  private void addSelectOptionsToModel(Model model) {
    List<ListCategoryDTO> categories = categoryService.getCategorySelectOptions();
    List<ProductTypeDTO> productTypes = productService.getAllProductTypes();
    model.addAttribute("categories", categories);
    model.addAttribute("productTypes", productTypes);
  }

  @GetMapping
  public String indexProduct(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long productTypeId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "5") int perPage,
      Model model) {
    SearchProductDTO params = new SearchProductDTO(name, categoryId, productTypeId, page, perPage);
    Page<ListProductDTO> products = productService.getAllProducts(params);
    addSelectOptionsToModel(model);
    model.addAttribute("search", params);
    model.addAttribute("products", products);
    model.addAttribute("totalPages", products.getTotalPages());

    return "admin/products/index";
  }

  @GetMapping("/{id}")
  public String detailProduct(@NonNull @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      DetailProductDTO product = productService.getProductById(id);
      model.addAttribute("product", product);

      return "admin/products/detail";
    } catch (ResourceNotFoundException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/products";
    }
  }

  @GetMapping("/new")
  public String newProduct(
      Model model) {
    addSelectOptionsToModel(model);
    model.addAttribute("product", new CreateProductDTO());

    return "admin/products/new";
  }

  @PostMapping("/new")
  public String createProduct(
      @Valid @ModelAttribute("product") CreateProductDTO createProductDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      addSelectOptionsToModel(model);
      model.addAttribute("failedMessage", MessageCode.CREATE_FAILED.getCode());
      return "admin/products/new";
    }
    productService.createProduct(createProductDTO);
    redirectAttributes.addFlashAttribute("successMessage", MessageCode.CREATE_SUCCESS.getCode());

    return "redirect:/admin/products";
  }

  @GetMapping("/{id}/edit")
  public String editProduct(@NonNull @PathVariable Long id, Model model) {
    addSelectOptionsToModel(model);
    DetailProductDTO product = productService.getProductById(id);
    UpdateProductDTO updateProductDTO = modelMapper.map(product, UpdateProductDTO.class);

    if (product.getCategory() != null) {
      updateProductDTO.setCategoryId(product.getCategory().getId());
    }
    if (product.getProductType() != null) {
      updateProductDTO.setProductTypeId(product.getProductType().getId());
    }

    model.addAttribute("product", updateProductDTO);

    return "admin/products/edit";
  }

  @PostMapping("/{id}/edit")
  public String updateProduct(
      @PathVariable Long id,
      @Valid @ModelAttribute("product") UpdateProductDTO updateProductDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      addSelectOptionsToModel(model);
      model.addAttribute("failedMessage", MessageCode.UPDATE_FAILED.getCode());

      return "admin/products/edit";
    }

    productService.updateProduct(updateProductDTO);
    redirectAttributes.addFlashAttribute("successMessage", MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/products";
  }

  @GetMapping("/{id}/delete")
  public String deleteProduct(
      @PathVariable Long id,
      RedirectAttributes redirectAttributes,
      Model model) {
    try {
      productService.deleteProduct(id);
      redirectAttributes.addFlashAttribute("successMessage", MessageCode.DELETE_SUCCESS.getCode());

      return "redirect:/admin/products";
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/products";
    }
  }
}
