package com.foodorder.foodapp.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResponseDTO<T> {
  private List<T> items;

  private int currentPage;

  private int perPage;

  private long totalElements;

  private int totalPages;

  public PageResponseDTO(Page<T> page) {
    this.items = page.getContent();
    this.currentPage = page.getNumber() + 1;
    this.perPage = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
  }
}
