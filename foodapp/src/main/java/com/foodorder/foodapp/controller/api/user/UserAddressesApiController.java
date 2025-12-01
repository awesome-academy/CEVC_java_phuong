package com.foodorder.foodapp.controller.api.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.user_address.CreateUserAddressDTO;
import com.foodorder.foodapp.dto.user_address.DetailUserAddressDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.service.ClientUserAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users-addresses")
@AllArgsConstructor
@Validated
@Tag(name = "user-addresses", description = "Api Client for user addresses")
@SecurityRequirement(name = "bearerAuth")
public class UserAddressesApiController {
  private final ClientUserAddressService clientUserAddressService;

  @GetMapping
  @Operation(summary = "User list user address", description = "Get list user address information")
  public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User currentUser) {
    List<DetailUserAddressDTO> response = clientUserAddressService.getAllUserAddress(currentUser);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/create")
  @Operation(summary = "Create user address", description = "Add a new user address")
  public ResponseEntity<?> createUserAddress(
      @Valid @RequestBody CreateUserAddressDTO createUserAddressDTO,
      @AuthenticationPrincipal User currentUser) {

    DetailUserAddressDTO response = clientUserAddressService.createUserAddress(currentUser, createUserAddressDTO);

    return ApiResponseDTO.created(response, "/api/user-addresses/create/" + response.getId());
  }

  @PostMapping("/{id}/update")
  @Operation(summary = "Update user address", description = "Update a user address")
  public ResponseEntity<?> updateUserAddress(
      @PathVariable Long id,
      @Valid @RequestBody CreateUserAddressDTO updateUserAddressDTO,
      @AuthenticationPrincipal User currentUser) {

    DetailUserAddressDTO response = clientUserAddressService.updateUserAddress(currentUser, id, updateUserAddressDTO);

    return ApiResponseDTO.ok(response);
  }

  @PostMapping("/{id}/delete")
  @Operation(summary = "Delete user address", description = "Delete a user address")
  public ResponseEntity<?> deleteUserAddress(
      @PathVariable Long id,
      @AuthenticationPrincipal User currentUser) {

    clientUserAddressService.deleteUserAddress(currentUser, id);

    return ApiResponseDTO.noContent();
  }
}
