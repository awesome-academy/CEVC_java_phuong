package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.dto.suggestion.AdminSearchSuggestionDTO;
import com.foodorder.foodapp.dto.suggestion.AdminUpdateSuggestionStatusDTO;
import com.foodorder.foodapp.dto.suggestion.DetailSuggestionDTO;
import com.foodorder.foodapp.enums.ESuggestion;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.service.SuggestionService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/suggestions")
@AllArgsConstructor
public class SuggestionsController {
  private final SuggestionService suggestionService;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model) {
    model.addAttribute("statuses", ESuggestion.values());
  }

  @GetMapping
  public String indexSuggestion(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "${app.pagination.default-page}") int page,
      @RequestParam(defaultValue = "${app.pagination.default-per-page}") int perPage,
      Model model) {
    AdminSearchSuggestionDTO params = new AdminSearchSuggestionDTO(status, page, perPage);
    Page<DetailSuggestionDTO> suggestions = suggestionService.getAllSuggestions(params);
    model.addAttribute("search", params);
    model.addAttribute("suggestions", suggestions);
    model.addAttribute("totalPages", suggestions.getTotalPages());

    return "admin/suggestions/index";
  }

  @PostMapping("/{id}/update-status")
  public String updateSuggestionStatus(
      @NonNull @PathVariable Long id,
      @Valid AdminUpdateSuggestionStatusDTO updateSuggestionStatusDTO,
      RedirectAttributes redirectAttributes,
      Model model) {
    suggestionService.updateSuggestionStatus(id, updateSuggestionStatusDTO);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/suggestions";
  }
}
