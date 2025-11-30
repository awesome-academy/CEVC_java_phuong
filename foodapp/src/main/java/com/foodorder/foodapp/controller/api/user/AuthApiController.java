package com.foodorder.foodapp.controller.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.auth.LoginJwtDTO;
import com.foodorder.foodapp.dto.auth.RefreshTokenJwtDTO;
import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.service.AuthService;
import com.foodorder.foodapp.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
}
