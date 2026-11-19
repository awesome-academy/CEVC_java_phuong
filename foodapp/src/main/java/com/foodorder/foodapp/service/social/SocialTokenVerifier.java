package com.foodorder.foodapp.service.social;

import com.foodorder.foodapp.enums.ESocialProvider;

/**
 * Checks a token issued by one social provider and returns who it belongs to. One implementation
 * per provider; SocialAuthService picks it by {@link #provider()}.
 */
public interface SocialTokenVerifier {
  ESocialProvider provider();

  /**
   * @throws com.foodorder.foodapp.exception.UnauthorizedException when the token is not valid for
   *     this app (bad signature, expired, issued for another client)
   * @throws com.foodorder.foodapp.exception.BadRequestException when the provider is not configured
   */
  SocialProfile verify(String token);
}
