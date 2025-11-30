package com.foodorder.foodapp.controller.api.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.response.ApiResponseDTO;
import com.foodorder.foodapp.dto.user.DetailUserDTO;
import com.foodorder.foodapp.dto.user.RegisterUserDTO;
import com.foodorder.foodapp.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/users")
@Validated
@AllArgsConstructor
@Tag(name = "users", description = "Api Client for sers")
public class RegisterApiController {
  private final UserService userService;

  @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Register user", description = "Register a new user")
  public ResponseEntity<?> register(
      @Valid RegisterUserDTO registerUserDTO,
      BindingResult bindingResult) {

    DetailUserDTO response = userService.registerUser(registerUserDTO);
    return ApiResponseDTO.created(response, "/api/users/" + response.getId());
  }
}
