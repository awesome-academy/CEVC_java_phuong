package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.dto.evaluate.AdminSearchEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.DetailEvaluateDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.service.EvaluateService;
import com.foodorder.foodapp.service.ProductService;

import io.micrometer.common.lang.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/evaluates")
@AllArgsConstructor
public class EvaluatesController {
  private final EvaluateService evaluateService;
  private final ProductService productService;

  @GetMapping
  public String indexEvaluate(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) Long ratingLevel,
      @RequestParam(required = false) Boolean isShow,
      Pageable pageable,
      Model model) {
    AdminSearchEvaluateDTO params = new AdminSearchEvaluateDTO(productId, ratingLevel, isShow);
    Page<DetailEvaluateDTO> evaluates = evaluateService.getAllEvaluates(params, pageable);
    model.addAttribute("search", params);
    model.addAttribute("products", productService.getProductSelectOptions());
    model.addAttribute("evaluates", evaluates);
    model.addAttribute("totalPages", evaluates.getTotalPages());

    return "admin/evaluates/index";
  }

  @PostMapping("/{id}/toggle-display")
  public String toggleDisplay(
      @NonNull @PathVariable Long id,
      RedirectAttributes redirectAttributes) {
    evaluateService.toggleDisplay(id);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/evaluates";
  }
}
