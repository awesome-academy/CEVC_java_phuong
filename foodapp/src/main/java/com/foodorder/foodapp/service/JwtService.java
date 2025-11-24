package com.foodorder.foodapp.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foodorder.foodapp.dto.auth.RefreshTokenJwtDTO;
import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.exception.UnauthorizedException;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.access-token-validity}")
  private long accessTokenValidity;

  @Value("${app.jwt.refresh-token-validity}")
  private long refreshTokenValidity;
  private Key key;

  private final UserRepository userRepository;

  public JwtService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostConstruct
  private void init() {
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public JwtResponseDTO refreshToken(RefreshTokenJwtDTO refreshTokenJwtDTO) {
    String refreshToken = refreshTokenJwtDTO.getRefreshToken();

    if (!validateToken(refreshToken)) {
      throw new UnauthorizedException("validation.user.refresh_token.invalid");
    }

    String email = getEmailFromToken(refreshToken);
    User user = userRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UnauthorizedException("validation.user.refresh_token.invalid"));
    String newAccessToken = generateAccessToken(email);
    String newRefreshToken = generateRefreshToken(email);

    return new JwtResponseDTO(user.getId(), email, newAccessToken, newRefreshToken);
  }

  public String generateAccessToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  public String getEmailFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }
}
