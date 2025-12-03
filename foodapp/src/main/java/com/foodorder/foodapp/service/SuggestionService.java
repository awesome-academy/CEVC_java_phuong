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

    ESuggestion status = suggestion.getStatus();
    if (status == ESuggestion.APPROVED || status == ESuggestion.REJECTED) {
      throw new BadRequestException("suggestion.update.not.allowed");
    }

    suggestion.setStatus(updateSuggestionStatusDTO.getStatus());
    suggestionRepository.save(suggestion);
  }
}
