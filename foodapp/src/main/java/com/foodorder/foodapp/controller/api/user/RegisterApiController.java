package com.foodorder.foodapp.controller.api.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.foodapp.dto.user.DetailUserDTO;
import com.foodorder.foodapp.dto.user.RegisterUserDTO;
import com.foodorder.foodapp.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/register")
@Validated
@AllArgsConstructor
public class RegisterApiController {
  private final UserService userService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public DetailUserDTO register(
      @Valid RegisterUserDTO registerUserDTO,
      BindingResult bindingResult) {
    return userService.registerUser(registerUserDTO);
  }
}
