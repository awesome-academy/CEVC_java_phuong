package com.foodorder.foodapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.exception.UnauthorizedException;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;

@Service
public class AuthService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public JwtResponseDTO login(String email, String password) {
    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UnauthorizedException("validation.user.email_or_password.invalid"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new UnauthorizedException("validation.user.email_or_password.invalid");
    }

    String accessToken = jwtService.generateAccessToken(email);
    String refreshToken = jwtService.generateRefreshToken(email);

    return new JwtResponseDTO(user.getId(), user.getEmail(), accessToken, refreshToken);
  }

}
