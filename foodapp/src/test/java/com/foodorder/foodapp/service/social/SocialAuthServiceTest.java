package com.foodorder.foodapp.service.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.enums.ESocialProvider;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.UnauthorizedException;
import com.foodorder.foodapp.model.AuthProvider;
import com.foodorder.foodapp.model.Role;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.AuthProviderRepository;
import com.foodorder.foodapp.repository.RoleRepository;
import com.foodorder.foodapp.repository.UserRepository;
import com.foodorder.foodapp.service.AuthService;

class SocialAuthServiceTest {
  private static final String TOKEN = "google-id-token";
  private static final String EMAIL = "an@gmail.com";

  private final SocialTokenVerifier googleVerifier = mock(SocialTokenVerifier.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final RoleRepository roleRepository = mock(RoleRepository.class);
  private final AuthProviderRepository authProviderRepository = mock(AuthProviderRepository.class);
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
  private final AuthService authService = mock(AuthService.class);
  private SocialAuthService service;

  @BeforeEach
  void setUp() {
    when(googleVerifier.provider()).thenReturn(ESocialProvider.GOOGLE);
    when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}random");
    when(roleRepository.findByName("USER")).thenReturn(Optional.of(role("USER")));
    when(authProviderRepository.findByName("GOOGLE")).thenReturn(Optional.of(provider("GOOGLE")));
    when(authService.issueTokens(any())).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return new JwtResponseDTO(user.getId(), user.getEmail(), "access", "refresh");
    });
    service = new SocialAuthService(List.of(googleVerifier), userRepository, roleRepository,
        authProviderRepository, passwordEncoder, authService);
  }

  @Test
  void login_firstTime_createsGoogleUserWithUnusablePassword() {
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, "Nguyễn Văn An"));
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    JwtResponseDTO response = service.login(ESocialProvider.GOOGLE, TOKEN);

    ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(saved.capture());
    assertThat(saved.getValue().getEmail()).isEqualTo(EMAIL);
    assertThat(saved.getValue().getFullName()).isEqualTo("Nguyễn Văn An");
    assertThat(saved.getValue().getRole().getName()).isEqualTo("USER");
    assertThat(saved.getValue().getAuthProvider().getName()).isEqualTo("GOOGLE");
    assertThat(saved.getValue().getPassword()).isEqualTo("{bcrypt}random");
    assertThat(response.getAccessToken()).isEqualTo("access");
  }

  @Test
  void login_noNameFromProvider_usesEmailPrefix() {
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, null));
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.login(ESocialProvider.GOOGLE, TOKEN);

    ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(saved.capture());
    assertThat(saved.getValue().getFullName()).isEqualTo("an");
  }

  @Test
  void login_existingGoogleUser_signsInWithoutCreating() {
    User existing = user("GOOGLE", null);
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, "An"));
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existing));

    JwtResponseDTO response = service.login(ESocialProvider.GOOGLE, TOKEN);

    assertThat(response.getId()).isEqualTo(existing.getId());
    verify(userRepository, never()).save(any());
  }

  @Test
  void login_emailRegisteredWithPassword_isRefused() {
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, "An"));
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user("LOCAL", null)));

    assertThatThrownBy(() -> service.login(ESocialProvider.GOOGLE, TOKEN))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("auth.social.email.registered_with_password");
    verify(authService, never()).issueTokens(any());
  }

  @Test
  void login_deletedAccount_isRefused() {
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, "An"));
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user("GOOGLE", LocalDateTime.now())));

    assertThatThrownBy(() -> service.login(ESocialProvider.GOOGLE, TOKEN))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("auth.social.account.disabled");
  }

  @Test
  void login_unverifiedEmail_isRefusedBeforeAnyLookup() {
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, false, "An"));

    assertThatThrownBy(() -> service.login(ESocialProvider.GOOGLE, TOKEN))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("auth.social.email.unverified");
    verify(userRepository, never()).findByEmailIgnoreCase(anyString());
  }

  @Test
  void login_concurrentFirstLogin_usesTheUserCreatedByTheOtherRequest() {
    User createdMeanwhile = user("GOOGLE", null);
    when(googleVerifier.verify(TOKEN)).thenReturn(new SocialProfile(EMAIL, true, "An"));
    when(userRepository.findByEmailIgnoreCase(EMAIL))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(createdMeanwhile));
    when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("uk email"));

    assertThat(service.login(ESocialProvider.GOOGLE, TOKEN).getId()).isEqualTo(createdMeanwhile.getId());
  }

  @Test
  void fromPath_isCaseInsensitiveAndRejectsUnknown() {
    assertThat(ESocialProvider.fromPath("google")).contains(ESocialProvider.GOOGLE);
    assertThat(ESocialProvider.fromPath("GOOGLE")).contains(ESocialProvider.GOOGLE);
    assertThat(ESocialProvider.fromPath("twitter")).isEmpty();
  }

  private static User user(String providerName, LocalDateTime deletedAt) {
    User user = new User();
    user.setId(42L);
    user.setEmail(EMAIL);
    user.setAuthProvider(provider(providerName));
    user.setDeletedAt(deletedAt);
    return user;
  }

  private static AuthProvider provider(String name) {
    AuthProvider provider = new AuthProvider();
    provider.setName(name);
    return provider;
  }

  private static Role role(String name) {
    Role role = new Role();
    role.setName(name);
    return role;
  }
}
