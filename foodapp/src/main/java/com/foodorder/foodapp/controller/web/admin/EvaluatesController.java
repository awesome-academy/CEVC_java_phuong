package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.dto.evaluate.AdminSearchEvaluateDTO;
import com.foodorder.foodapp.dto.evaluate.DetailEvaluateDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.service.EvaluateService;

import io.micrometer.common.lang.NonNull;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/evaluates")
@AllArgsConstructor
public class EvaluatesController {
  private final EvaluateService evaluateService;

  @GetMapping
  public String indexEvaluate(
      @RequestParam(required = false) String productName,
      @RequestParam(required = false) Long ratingLevel,
      @RequestParam(defaultValue = "${app.pagination.default-page}") int page,
      @RequestParam(defaultValue = "${app.pagination.default-per-page}") int perPage,
      Model model) {
    AdminSearchEvaluateDTO params = new AdminSearchEvaluateDTO(productName, ratingLevel, page, perPage);
    Page<DetailEvaluateDTO> evaluates = evaluateService.getAllEvaluates(params);
    model.addAttribute("search", params);
    model.addAttribute("evaluates", evaluates);
    model.addAttribute("totalPages", evaluates.getTotalPages());

    return "admin/evaluates/index";
  }

  @GetMapping("/{id}/hide-evaluate")
  public String hideEvaluate(
      @NonNull @PathVariable Long id,
      RedirectAttributes redirectAttributes,
      Model model) {
    evaluateService.updateEvaluateDisplayStatus(id);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/evaluates";
  }
}
