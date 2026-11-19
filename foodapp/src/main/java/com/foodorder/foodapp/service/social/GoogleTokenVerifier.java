package com.foodorder.foodapp.service.social;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.foodorder.foodapp.config.properties.SocialAuthProperties;
import com.foodorder.foodapp.enums.ESocialProvider;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.UnauthorizedException;

/**
 * Google sign-in: the client sends the ID token (a JWT) it got from Google Identity Services.
 * Checked locally, no call per login: signature against Google's public keys (cached by Nimbus),
 * issuer, audience = our client id (a token issued to another app is refused) and expiry.
 */
@Component
public class GoogleTokenVerifier implements SocialTokenVerifier {
  static final String JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
  private static final Duration JWK_SET_TIMEOUT = Duration.ofSeconds(5);
  private static final Set<String> ISSUERS = Set.of("accounts.google.com", "https://accounts.google.com");

  /** null while GOOGLE_CLIENT_ID is not set. */
  private final JwtDecoder decoder;

  @Autowired
  public GoogleTokenVerifier(SocialAuthProperties properties) {
    this(properties.google() != null && properties.google().configured()
        ? withGoogleValidation(
            NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).restOperations(jwkSetRestTemplate()).build(),
            properties.google().clientId())
        : null);
  }

  /** The default RestTemplate has no timeouts: a slow Google endpoint would hang login requests. */
  private static RestTemplate jwkSetRestTemplate() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(JWK_SET_TIMEOUT);
    requestFactory.setReadTimeout(JWK_SET_TIMEOUT);
    return new RestTemplate(requestFactory);
  }

  /** Decoder injectable so tests can sign tokens with their own key. */
  GoogleTokenVerifier(JwtDecoder decoder) {
    this.decoder = decoder;
  }

  @Override
  public ESocialProvider provider() {
    return ESocialProvider.GOOGLE;
  }

  @Override
  public SocialProfile verify(String token) {
    if (decoder == null) {
      throw new BadRequestException("auth.social.provider.not_configured", "Google");
    }

    Jwt jwt;
    try {
      jwt = decoder.decode(token);
    } catch (BadJwtException e) {
      // Malformed, bad signature, expired, wrong issuer or audience
      throw new UnauthorizedException("auth.social.token.invalid");
    } catch (JwtException e) {
      // Google's keys could not be fetched: our problem, not the client's token
      throw new IllegalStateException("Could not verify the Google ID token", e);
    }

    Object emailVerified = jwt.getClaim("email_verified");
    return new SocialProfile(jwt.getClaimAsString("email"),
        Boolean.TRUE.equals(emailVerified) || "true".equals(String.valueOf(emailVerified)),
        jwt.getClaimAsString("name"));
  }

  /** Google ID token rules on top of the signature check done by the decoder. */
  static NimbusJwtDecoder withGoogleValidation(NimbusJwtDecoder decoder, String clientId) {
    OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
        new JwtTimestampValidator(),
        new JwtClaimValidator<Object>(JwtClaimNames.ISS, iss -> iss != null && ISSUERS.contains(iss.toString())),
        new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, aud -> aud != null && aud.contains(clientId)));
    decoder.setJwtValidator(validator);
    return decoder;
  }
}
