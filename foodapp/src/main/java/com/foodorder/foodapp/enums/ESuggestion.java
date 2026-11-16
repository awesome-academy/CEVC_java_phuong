package com.foodorder.foodapp.enums;

/**
 * Suggestion status and its transition rules (single source of truth for both admin and owner).
 * Flow: PENDING -> REVIEWED -> APPROVED / REJECTED (PENDING may also go straight to APPROVED / REJECTED).
 */
public enum ESuggestion {
  PENDING,
  REVIEWED,
  APPROVED,
  REJECTED;

  /** Final statuses: admin can no longer change them. */
  public boolean isFinal() {
    return this == APPROVED || this == REJECTED;
  }

  /** Owner cannot edit or delete an approved suggestion; a rejected one can be edited and re-submitted. */
  public boolean isLockedForOwner() {
    return this == APPROVED;
  }

  /** Whether admin may move a suggestion from this status to {@code next}. */
  public boolean canMoveTo(ESuggestion next) {
    return switch (this) {
      case PENDING -> next == REVIEWED || next == APPROVED || next == REJECTED;
      case REVIEWED -> next == APPROVED || next == REJECTED;
      case APPROVED, REJECTED -> false;
    };
  }
}
