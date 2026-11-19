package com.foodorder.foodapp.service.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.UnauthorizedException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Real signed tokens (own RSA key instead of Google's) through the production validator:
 * signature, issuer, audience and expiry are all exercised, no network.
 */
class GoogleTokenVerifierTest {
  private static final String CLIENT_ID = "test-client.apps.googleusercontent.com";

  private static KeyPair googleKey;
  private static KeyPair otherKey;
  private static GoogleTokenVerifier verifier;

  @BeforeAll
  static void setUp() throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    googleKey = generator.generateKeyPair();
    otherKey = generator.generateKeyPair();
    verifier = new GoogleTokenVerifier(GoogleTokenVerifier.withGoogleValidation(
        NimbusJwtDecoder.withPublicKey((RSAPublicKey) googleKey.getPublic()).build(), CLIENT_ID));
  }

  @Test
  void verify_validToken_returnsProfile() throws Exception {
    SocialProfile profile = verifier.verify(token(claims().build(), googleKey));

    assertThat(profile.email()).isEqualTo("an@gmail.com");
    assertThat(profile.emailVerified()).isTrue();
    assertThat(profile.name()).isEqualTo("Nguyễn Văn An");
  }

  @Test
  void verify_issuerWithoutScheme_isAccepted() throws Exception {
    assertThat(verifier.verify(token(claims().issuer("accounts.google.com").build(), googleKey)).email())
        .isEqualTo("an@gmail.com");
  }

  @Test
  void verify_unverifiedEmail_isReportedNotHidden() throws Exception {
    assertThat(verifier.verify(token(claims().claim("email_verified", false).build(), googleKey)).emailVerified())
        .isFalse();
  }

  @Test
  void verify_tokenIssuedForAnotherApp_isRejected() throws Exception {
    assertInvalid(token(claims().audience("someone-else.apps.googleusercontent.com").build(), googleKey));
  }

  @Test
  void verify_expiredToken_isRejected() throws Exception {
    assertInvalid(token(claims().expirationTime(Date.from(Instant.now().minusSeconds(3600))).build(), googleKey));
  }

  @Test
  void verify_otherIssuer_isRejected() throws Exception {
    assertInvalid(token(claims().issuer("https://evil.example.com").build(), googleKey));
  }

  @Test
  void verify_signedWithAnotherKey_isRejected() throws Exception {
    assertInvalid(token(claims().build(), otherKey));
  }

  @Test
  void verify_notAJwt_isRejected() {
    assertInvalid("not-a-token");
  }

  @Test
  void verify_clientIdNotConfigured_isBadRequest() {
    assertThatThrownBy(() -> new GoogleTokenVerifier((org.springframework.security.oauth2.jwt.JwtDecoder) null)
        .verify("anything"))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("auth.social.provider.not_configured");
  }

  private static void assertInvalid(String token) {
    assertThatThrownBy(() -> verifier.verify(token))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("auth.social.token.invalid");
  }

  private static JWTClaimsSet.Builder claims() {
    Instant now = Instant.now();
    return new JWTClaimsSet.Builder()
        .issuer("https://accounts.google.com")
        .audience(CLIENT_ID)
        .subject("1234567890")
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusSeconds(3600)))
        .claim("email", "an@gmail.com")
        .claim("email_verified", true)
        .claim("name", "Nguyễn Văn An");
  }

  private static String token(JWTClaimsSet claims, KeyPair key) throws Exception {
    SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
    jwt.sign(new RSASSASigner((RSAPrivateKey) key.getPrivate()));
    return jwt.serialize();
  }
}
