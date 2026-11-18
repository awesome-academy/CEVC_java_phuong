package com.foodorder.foodapp.controller.web.admin;

import com.foodorder.foodapp.config.properties.PaginationProperties;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

import com.foodorder.foodapp.dto.suggestion.AdminSearchSuggestionDTO;
import com.foodorder.foodapp.dto.suggestion.AdminUpdateSuggestionStatusDTO;
import com.foodorder.foodapp.dto.suggestion.DetailSuggestionDTO;
import com.foodorder.foodapp.enums.ESuggestion;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.exception.BadRequestException;
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
  private final PaginationProperties paginationProperties;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model) {
    model.addAttribute("statuses", ESuggestion.values());
  }

  @GetMapping
  public String indexSuggestion(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer perPage,
      Model model) {
    AdminSearchSuggestionDTO params = new AdminSearchSuggestionDTO(status, paginationProperties.resolve(page, perPage));
    Page<DetailSuggestionDTO> suggestions = suggestionService.getAllSuggestions(params);
    model.addAttribute("search", params);
    model.addAttribute("suggestions", suggestions);
    model.addAttribute("totalPages", suggestions.getTotalPages());

    return "admin/suggestions/index";
  }

  @GetMapping("/{id}")
  public String detailSuggestion(@NonNull @PathVariable Long id, Model model) {
    model.addAttribute("suggestion", suggestionService.getSuggestionById(id));

    return "admin/suggestions/detail";
  }

  @PostMapping("/{id}/update-status")
  public String updateSuggestionStatus(
      @NonNull @PathVariable Long id,
      @Valid AdminUpdateSuggestionStatusDTO updateSuggestionStatusDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("failedMessage",
          MessageCode.UPDATE_FAILED.getCode());
      return "redirect:/admin/suggestions";
    }

    try {
      suggestionService.updateSuggestionStatus(id, updateSuggestionStatusDTO);
      redirectAttributes.addFlashAttribute("successMessage",
          MessageCode.UPDATE_SUCCESS.getCode());
    } catch (BadRequestException e) {
      // Show business rule errors as toast instead of the JSON error response
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
    } catch (ObjectOptimisticLockingFailureException e) {
      // Suggestion was changed (by the owner or another admin) after it was loaded
      redirectAttributes.addFlashAttribute("failedMessage", "suggestion.update.conflict");
    }

    return "redirect:/admin/suggestions";
  }
}
