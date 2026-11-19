package com.foodorder.foodapp.controller.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.auth.LoginJwtDTO;
import com.foodorder.foodapp.dto.auth.RefreshTokenJwtDTO;
import com.foodorder.foodapp.dto.auth.SocialLoginDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.enums.ESocialProvider;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.service.AuthService;
import com.foodorder.foodapp.service.JwtService;
import com.foodorder.foodapp.service.social.SocialAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Validated
@AllArgsConstructor
@Tag(name = "auth", description = "Api Client for Auth")
public class AuthApiController {
  private final AuthService authService;
  private final JwtService jwtService;
  private final SocialAuthService socialAuthService;

  @PostMapping("/login")
  @Operation(summary = "User login", description = "Login user and return JWT token")
  public ResponseEntity<?> login(
      @Valid @RequestBody LoginJwtDTO loginUserDTO) {

    JwtResponseDTO response = authService.login(loginUserDTO.getEmail(), loginUserDTO.getPassword());
    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/refresh")
  @Operation(summary = "User refresh token", description = "Refresh JWT token for user")
  public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenJwtDTO refreshTokenJwtDTO) {

    JwtResponseDTO response = jwtService.refreshToken(refreshTokenJwtDTO);
    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/social/{provider}")
  @Operation(summary = "Social login", description = """
      Sign in with a social account and get the same JWT pair as /api/auth/login.

      Supported provider: **google**.

      **Google**: on the client, sign in with Google Identity Services (web) or Google Sign-In
      (mobile) using this app's OAuth client id, then send the returned **ID token**
      (`credential`) as `token`. The server checks its signature, issuer, audience and expiry.

      The first login creates a USER account from the Google email (which must be verified) and
      name. An email already registered with a password cannot sign in this way.""")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Signed in: data = {id, email, accessToken, refreshToken}"),
      @ApiResponse(responseCode = "400", description = "Unknown or unconfigured provider, missing token, "
          + "or email already registered with a password / another provider", content = @Content),
      @ApiResponse(responseCode = "401", description = "Invalid or expired token, unverified email, "
          + "or disabled account", content = @Content)
  })
  public ResponseEntity<?> socialLogin(
      @Parameter(description = "Login provider", schema = @Schema(allowableValues = { "google" }))
      @PathVariable String provider,
      @Valid @RequestBody SocialLoginDTO socialLoginDTO) {
    ESocialProvider socialProvider = ESocialProvider.fromPath(provider)
        .orElseThrow(() -> new BadRequestException("auth.social.provider.unsupported", provider));

    JwtResponseDTO response = socialAuthService.login(socialProvider, socialLoginDTO.getToken());
    return ApiResponseDTO.ok(response);
  }
}
