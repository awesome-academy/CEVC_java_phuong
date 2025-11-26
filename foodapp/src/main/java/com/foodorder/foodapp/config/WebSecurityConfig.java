package com.foodorder.foodapp.config;

import com.foodorder.foodapp.repository.AuthProviderRepository;
import com.foodorder.foodapp.security.JwtFilter;

import lombok.AllArgsConstructor;

import java.util.List;

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

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

  private final AuthProviderRepository authProviderRepository;
  private final JwtFilter jwtFilter;

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
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  @Order(2) // Filter admin/**
  public SecurityFilterChain webSecurityChain(HttpSecurity http, List<String> publicStaticUrls) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(publicStaticUrls.toArray(new String[0])).permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/admin/login")
            .loginProcessingUrl("/admin/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .defaultSuccessUrl("/admin/users")
            .failureUrl("/admin/login?error")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/admin/logout")
            .logoutSuccessUrl("/admin/login?logout=true")
            .deleteCookies("JSESSIONID")
            .permitAll());
    return http.build();
  }

}
