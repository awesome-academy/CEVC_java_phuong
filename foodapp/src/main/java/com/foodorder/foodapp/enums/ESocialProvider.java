package com.foodorder.foodapp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * Social login providers the API accepts (POST /api/auth/social/{provider}). Names match
 * auth_providers.name. Adding one: a constant here plus a SocialTokenVerifier for it.
 */
public enum ESocialProvider {
  GOOGLE;

  /** Case-insensitive lookup of the {provider} path segment ("google", "GOOGLE"). */
  public static Optional<ESocialProvider> fromPath(String value) {
    return Arrays.stream(values())
        .filter(provider -> provider.name().equalsIgnoreCase(value))
        .findFirst();
  }
}
