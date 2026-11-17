package com.foodorder.foodapp.config;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.foodorder.foodapp.repository.UserRepository;
import com.foodorder.foodapp.security.JwtFilter;
import com.foodorder.foodapp.service.JwtService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final MessageSource messageSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(1) // Filter api/**
  public SecurityFilterChain apiSecurityChain(HttpSecurity http, List<String> publicApiUrls) throws Exception {
    http
        .securityMatcher("/api/**")
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(publicApiUrls.toArray(new String[0])).permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(new JwtFilter(userRepository, jwtService, messageSource, publicApiUrls),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  @Order(2) // Filter admin/**
  public SecurityFilterChain webSecurityChain(HttpSecurity http, List<String> publicStaticUrls) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(publicStaticUrls.toArray(new String[0])).permitAll()
            .requestMatchers("/admin/login").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/admin/login")
            .loginProcessingUrl("/admin/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .defaultSuccessUrl("/admin/users")
            // Redirect only: failureUrl() would store the exception in the (JDBC) session,
            // and since Spring Security 6.5 it carries the typed password
            .failureHandler((request, response, exception) -> response
                .sendRedirect(request.getContextPath() + "/admin/login?error"))
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/admin/logout")
            .logoutSuccessUrl("/admin/login?logout=true")
            .deleteCookies("SESSION")
            .permitAll());
    return http.build();
  }

}
