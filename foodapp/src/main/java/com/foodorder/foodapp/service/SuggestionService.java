package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;

import com.foodorder.foodapp.repository.SuggestionRepository;

import com.foodorder.foodapp.specification.SuggestionSpecification;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.suggestion.AdminSearchSuggestionDTO;
import com.foodorder.foodapp.dto.suggestion.AdminUpdateSuggestionStatusDTO;
import com.foodorder.foodapp.dto.suggestion.DetailSuggestionDTO;
import com.foodorder.foodapp.enums.ESuggestion;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Suggestion;
import com.foodorder.foodapp.exception.BadRequestException;

import java.util.Objects;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SuggestionService {
  private final ModelMapper modelMapper;
  private final SuggestionRepository suggestionRepository;

  public Page<DetailSuggestionDTO> getAllSuggestions(AdminSearchSuggestionDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(),
        Sort.by("id").descending());
    Specification<Suggestion> spec = buildSpecification(params);
    Page<Suggestion> pageResult = suggestionRepository.findAll(spec, pageable);

    return pageResult.map(suggestion -> modelMapper.map(suggestion,
        DetailSuggestionDTO.class));
  }

  public DetailSuggestionDTO getSuggestionById(Long id) {
    Suggestion suggestion = suggestionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("suggestion.not.found"));

    return modelMapper.map(suggestion, DetailSuggestionDTO.class);
  }

  private Specification<Suggestion> buildSpecification(AdminSearchSuggestionDTO params) {
    return Stream.of(
        SuggestionSpecification.hasStatus(params.getStatus()),
        SuggestionSpecification.withFetch())
        .filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
  }

  public void updateSuggestionStatus(Long id, AdminUpdateSuggestionStatusDTO updateSuggestionStatusDTO) {
    Suggestion suggestion = suggestionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("suggestion.not.found"));

    ESuggestion currentStatus = suggestion.getStatus();
    if (currentStatus.isFinal()) {
      throw new BadRequestException("suggestion.update.not.allowed");
    }

    ESuggestion newStatus = updateSuggestionStatusDTO.getStatus();
    if (!currentStatus.canMoveTo(newStatus)) {
      throw new BadRequestException("suggestion.status.invalid");
    }

    String reasonReject = null;
    if (newStatus == ESuggestion.REJECTED) {
      reasonReject = updateSuggestionStatusDTO.getReasonReject();
      if (reasonReject == null || reasonReject.isBlank()) {
        throw new BadRequestException("validation.suggestion.reason_reject.required");
      }
      reasonReject = reasonReject.trim();
    }

    suggestion.setStatus(newStatus);
    suggestion.setReasonReject(reasonReject);
    suggestionRepository.save(suggestion);
  }
}
