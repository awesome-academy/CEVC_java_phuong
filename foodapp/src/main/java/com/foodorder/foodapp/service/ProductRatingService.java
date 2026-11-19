package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;

import com.foodorder.foodapp.repository.EvaluateRepository;
import com.foodorder.foodapp.repository.ProductRepository;

import lombok.AllArgsConstructor;

/**
 * Single place for the product average rating formula, shared by the client (create/delete)
 * and the admin (show/hide) evaluate flows.
 */
@Service
@AllArgsConstructor
public class ProductRatingService {
  private final EvaluateRepository evaluateRepository;
  private final ProductRepository productRepository;

  /**
   * Recomputes the average over visible evaluates and writes only the average_rating column,
   * so a stale Product entity in the persistence context can never overwrite other columns
   * (e.g. quantity changed by a concurrent order). Must run inside the caller's transaction.
   */
  public void recalculate(Long productId) {
    double averageRating = evaluateRepository.averageVisibleRating(productId);
    productRepository.updateAverageRating(productId, (float) averageRating);
  }
}
