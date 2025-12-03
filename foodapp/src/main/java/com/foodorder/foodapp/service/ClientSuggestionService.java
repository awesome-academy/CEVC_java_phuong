package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.foodorder.foodapp.exception.BadRequestException;
import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.suggestion.DetailSuggestionDTO;
import com.foodorder.foodapp.dto.suggestion.CreateSuggestionDTO;
import com.foodorder.foodapp.enums.ESuggestion;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Suggestion;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.SuggestionRepository;

@Service
@AllArgsConstructor
public class ClientSuggestionService {
  private final ModelMapper modelMapper;
  private final ImageUploadService imageUploadService;
  private final SuggestionRepository suggestionRepository;

  public List<DetailSuggestionDTO> getSuggestions(User currentUser) {
    List<Suggestion> suggestions = suggestionRepository.findByUserId(currentUser.getId());

    return suggestions.stream()
        .map(suggestion -> modelMapper.map(suggestion, DetailSuggestionDTO.class))
        .collect(Collectors.toList());
  }

  public DetailSuggestionDTO getSuggestion(User currentUser, Long suggestionId) {
    Suggestion suggestion = suggestionRepository.findByIdAndUserId(suggestionId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("suggestion.not.found"));

    return modelMapper.map(suggestion, DetailSuggestionDTO.class);
  }

  public DetailSuggestionDTO createSuggestion(User currentUser, CreateSuggestionDTO createSuggestionDTO) {
    Suggestion suggestion = new Suggestion();
    String newImagePath = null;

    try {
      newImagePath = imageUploadService.uploadImage(createSuggestionDTO.getImageFile());

      suggestion.setUser(currentUser);
      suggestion.setTitle(createSuggestionDTO.getTitle());
      suggestion.setDescription(createSuggestionDTO.getDescription());
      Optional.ofNullable(newImagePath).ifPresent(suggestion::setImage);

      suggestion = suggestionRepository.save(suggestion);

      return modelMapper.map(suggestion, DetailSuggestionDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newImagePath);
      throw e;
    }
  }

  public DetailSuggestionDTO updateSuggestion(User currentUser, Long suggestionId,
      CreateSuggestionDTO createSuggestionDTO) {
    Suggestion suggestion = suggestionRepository.findByIdAndUserId(suggestionId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("suggestion.not.found"));

    String newImagePath = null;
    String oldImagePath = suggestion.getImage();
    ESuggestion status = suggestion.getStatus();
    if (status == ESuggestion.APPROVED) {
      throw new BadRequestException("suggestion.update.not.allowed");
    }

    try {
      newImagePath = imageUploadService.uploadImage(createSuggestionDTO.getImageFile());

      suggestion.setUser(currentUser);
      suggestion.setTitle(createSuggestionDTO.getTitle());
      suggestion.setDescription(createSuggestionDTO.getDescription());
      Optional.ofNullable(newImagePath).ifPresent(suggestion::setImage);
      suggestion.setStatus(ESuggestion.PENDING);

      suggestion = suggestionRepository.save(suggestion);
      imageUploadService.deleteImage(oldImagePath, newImagePath != null);

      return modelMapper.map(suggestion, DetailSuggestionDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newImagePath);
      throw e;
    }
  }

  public void deleteSuggestion(User currentUser, Long suggestionId) {
    Suggestion suggestion = suggestionRepository.findByIdAndUserId(suggestionId, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("suggestion.not.found"));

    ESuggestion status = suggestion.getStatus();
    if (status == ESuggestion.APPROVED) {
      throw new BadRequestException("suggestion.update.not.allowed");
    }

    suggestionRepository.delete(suggestion);
  }
}
