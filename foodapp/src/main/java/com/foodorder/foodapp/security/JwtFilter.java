package com.foodorder.foodapp.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;
import com.foodorder.foodapp.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final MessageSource messageSource;

  private final UserRepository userRepository;
  private final List<String> excludeUrls;

  public JwtFilter(UserRepository userRepository, JwtService jwtService, MessageSource messageSource,
      List<String> publicStaticUrls, List<String> publicApiUrls) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.messageSource = messageSource;
    this.excludeUrls = Stream.concat(
        publicApiUrls.stream(),
        publicStaticUrls.stream())
        .collect(Collectors.toList());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      sendUnauthorized(response, "validation.user.token.invalid");
      return;
    }

    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      sendUnauthorized(response, "validation.user.token.invalid");
      return;
    }

    String email = jwtService.getEmailFromToken(token);
    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
    if (user == null) {
      sendUnauthorized(response, "validation.user.token.invalid");
      return;
    }

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
        new ArrayList<>());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

    String path = request.getServletPath();

    for (String url : this.excludeUrls) {
      if (url.endsWith("/**")) {
        String baseUrl = url.substring(0, url.length() - 3);
        if (path.startsWith(baseUrl)) {
          return true;
        }
      } else if (path.equals(url)) {
        return true;
      }
    }

    return false;
  }

  private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
    String localizedMessage = messageSource.getMessage(
        message,
        null,
        LocaleContextHolder.getLocale());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    Map<String, Object> body = new HashMap<>();
    body.put("status", 401);
    body.put("error", "Unauthorized");
    body.put("message", localizedMessage);

    ObjectMapper mapper = new ObjectMapper();
    response.getWriter().write(mapper.writeValueAsString(body));
    response.getWriter().flush();
  }
}
