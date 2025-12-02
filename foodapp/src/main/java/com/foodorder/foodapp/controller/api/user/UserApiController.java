package com.foodorder.foodapp.controller.api.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.user.ClientDetailUserDTO;
import com.foodorder.foodapp.dto.user.ClientUpdateUserDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Validated
@Tag(name = "users", description = "Api Client for Users")
@SecurityRequirement(name = "bearerAuth")
public class UserApiController {
  private final ClientUserService clientUserService;

  @GetMapping("/info")
  @Operation(summary = "User info", description = "Get user information")
  public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User currentUser) {
    ClientDetailUserDTO response = clientUserService.getUserById(currentUser);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Update info", description = "Update user information")
  public ResponseEntity<?> updateUserInfo(
      @Valid ClientUpdateUserDTO updateUserDTO,
      @AuthenticationPrincipal User currentUser) {

    ClientDetailUserDTO response = clientUserService.updateUser(currentUser, updateUserDTO);
    return ApiResponseDTO.ok(response);
  }
}
