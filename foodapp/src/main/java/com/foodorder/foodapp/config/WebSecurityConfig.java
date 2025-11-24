package com.foodorder.foodapp.config;

import com.foodorder.foodapp.repository.AuthProviderRepository;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

  private final AuthProviderRepository authProviderRepository;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/css/**", "/js/**", "/public/**", "/images/**").permitAll()
            .requestMatchers("/api/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())

        .formLogin((form) -> form
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
