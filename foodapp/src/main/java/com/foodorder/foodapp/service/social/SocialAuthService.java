package com.foodorder.foodapp.service.social;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodorder.foodapp.dto.response.JwtResponseDTO;
import com.foodorder.foodapp.enums.ESocialProvider;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.exception.UnauthorizedException;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.AuthProviderRepository;
import com.foodorder.foodapp.repository.RoleRepository;
import com.foodorder.foodapp.repository.UserRepository;
import com.foodorder.foodapp.service.AuthService;

/**
 * Social login (#20): verify the provider's token, then sign in the user with that email or
 * create them on first login, and answer with our own JWT pair (same as password login).
 *
 * <p>An account belongs to one way of signing in (users.auth_provider_id): an email already
 * registered with a password, or with another provider, is refused rather than taken over.
 */
@Service
public class SocialAuthService {
  private static final String LOCAL_PROVIDER = "LOCAL";
  private static final int FULL_NAME_MAX = 100;

  private final Map<ESocialProvider, SocialTokenVerifier> verifiers = new EnumMap<>(ESocialProvider.class);
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthProviderRepository authProviderRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  public SocialAuthService(List<SocialTokenVerifier> verifiers, UserRepository userRepository,
      RoleRepository roleRepository, AuthProviderRepository authProviderRepository,
      PasswordEncoder passwordEncoder, AuthService authService) {
    verifiers.forEach(verifier -> this.verifiers.put(verifier.provider(), verifier));
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.authProviderRepository = authProviderRepository;
    this.passwordEncoder = passwordEncoder;
    this.authService = authService;
  }

  public JwtResponseDTO login(ESocialProvider provider, String token) {
    SocialTokenVerifier verifier = verifiers.get(provider);
    if (verifier == null) {
      throw new BadRequestException("auth.social.provider.unsupported", provider.name().toLowerCase());
    }

    SocialProfile profile = verifier.verify(token);
    if (!profile.emailVerified() || profile.email() == null || profile.email().isBlank()) {
      throw new UnauthorizedException("auth.social.email.unverified");
    }

    User user = userRepository.findByEmailIgnoreCase(profile.email())
        .map(existing -> checkCanSignIn(existing, provider))
        .orElseGet(() -> createUser(profile, provider));

    return authService.issueTokens(user);
  }

  private User checkCanSignIn(User user, ESocialProvider provider) {
    if (user.getDeletedAt() != null) {
      throw new UnauthorizedException("auth.social.account.disabled");
    }

    String registeredWith = user.getAuthProvider().getName();
    if (LOCAL_PROVIDER.equals(registeredWith)) {
      throw new BadRequestException("auth.social.email.registered_with_password");
    }
    if (!provider.name().equals(registeredWith)) {
      throw new BadRequestException("auth.social.email.registered_with_provider", registeredWith.toLowerCase());
    }
    return user;
  }

  /**
   * First social login: a USER account with an unusable random password (the user never knows
   * it, so password login stays closed for this account).
   */
  private User createUser(SocialProfile profile, ESocialProvider provider) {
    User user = new User();
    user.setEmail(profile.email());
    user.setFullName(fullName(profile));
    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    user.setRole(roleRepository.findByName("USER")
        .orElseThrow(() -> new ResourceNotFoundException("role.not.found")));
    user.setAuthProvider(authProviderRepository.findByName(provider.name())
        .orElseThrow(() -> new ResourceNotFoundException("auth_provider.not.found")));

    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      // Two first logins at the same time: the other request created the user (UNIQUE email)
      return userRepository.findByEmailIgnoreCase(profile.email())
          .map(existing -> checkCanSignIn(existing, provider))
          .orElseThrow(() -> e);
    }
  }

  private static String fullName(SocialProfile profile) {
    String name = profile.name() != null && !profile.name().isBlank()
        ? profile.name().trim()
        : profile.email().substring(0, profile.email().indexOf('@'));
    return name.length() > FULL_NAME_MAX ? name.substring(0, FULL_NAME_MAX) : name;
  }
}
