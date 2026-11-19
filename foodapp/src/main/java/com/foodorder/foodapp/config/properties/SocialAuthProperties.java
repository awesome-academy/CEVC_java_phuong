package com.foodorder.foodapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * app.social-auth.*: credentials of the social login providers (#20). A provider whose
 * credentials are left empty answers "not configured" instead of accepting tokens.
 *
 * @param google Google sign-in: the OAuth client id the ID tokens are issued for (their "aud")
 */
@ConfigurationProperties(prefix = "app.social-auth")
public record SocialAuthProperties(Google google) {

  public record Google(String clientId) {
    public boolean configured() {
      return clientId != null && !clientId.isBlank();
    }
  }
}
