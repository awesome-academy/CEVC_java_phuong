package com.foodorder.foodapp.service;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import com.foodorder.foodapp.repository.*;
import com.foodorder.foodapp.specification.UserSpecification;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.auth_provider.AuthProviderDTO;
import com.foodorder.foodapp.dto.role.RoleDTO;
import com.foodorder.foodapp.dto.user.CreateUserDTO;
import com.foodorder.foodapp.dto.user.DetailUserDTO;
import com.foodorder.foodapp.dto.user.ListUserDTO;
import com.foodorder.foodapp.dto.user.SearchUserDTO;
import com.foodorder.foodapp.dto.user.UpdateUserDTO;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.model.Role;
import com.foodorder.foodapp.model.User;

import com.foodorder.foodapp.model.AuthProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@SuppressWarnings("null")
public class UserService {

  private final AuthProviderRepository authProviderRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final ImageUploadService imageUploadService;
  private final OrderRepository orderRepository;

  public Page<ListUserDTO> getAllUsers(SearchUserDTO params) {
    int pageIndex = Math.max(0, params.getPage() - 1);
    Pageable pageable = PageRequest.of(pageIndex, params.getPerPage(), Sort.by("id").descending());

    Specification<User> spec = Stream.of(
        UserSpecification.withFetch(),
        UserSpecification.hasFullName(params.getName()),
        UserSpecification.hasRole(params.getRoleId()),
        UserSpecification.notDeleted()).filter(Objects::nonNull)
        .reduce((s1, s2) -> s1.and(s2))
        .orElse(null);
    Page<User> pageResult = userRepository.findAll(spec, pageable);

    return pageResult.map(user -> modelMapper.map(user, ListUserDTO.class));
  }

  public List<RoleDTO> getAllRoles() {
    return roleRepository.findAll().stream()
        .map(role -> modelMapper.map(role, RoleDTO.class))
        .collect(Collectors.toList());
  }

  public List<AuthProviderDTO> getAllAuthProviders() {
    return authProviderRepository.findAll().stream()
        .map(authProvider -> modelMapper.map(authProvider, AuthProviderDTO.class))
        .collect(Collectors.toList());
  }

  public DetailUserDTO getUserById(@NonNull Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

    return modelMapper.map(user, DetailUserDTO.class);
  }

  public DetailUserDTO createUser(CreateUserDTO createUserDTO) {
    Long roleId = createUserDTO.getRoleId();
    Long authProviderId = createUserDTO.getAuthProviderId();
    String avatarPath = null;
    try {
      avatarPath = imageUploadService.uploadImage(createUserDTO.getAvatarFile());

      User user = new User();
      user.setFullName(createUserDTO.getFullName());
      user.setEmail(createUserDTO.getEmail());
      // TODO: Implement password hashing later.
      user.setPassword(createUserDTO.getPassword());
      user.setAvatar(avatarPath);
      user.setAge(createUserDTO.getAge());

      if (roleId != null) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("role.not.found"));
        user.setRole(role);
      }
      if (authProviderId != null) {
        AuthProvider authProvider = authProviderRepository.findById(authProviderId)
            .orElseThrow(() -> new ResourceNotFoundException("auth_provider.not.found"));
        user.setAuthProvider(authProvider);
      }

      user = userRepository.save(user);
      return modelMapper.map(user, DetailUserDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(avatarPath);
      throw e;
    }
  }

  public DetailUserDTO updateUser(UpdateUserDTO updateUserDTO) {
    User exitUser = userRepository.findById(updateUserDTO.getId())
        .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

    String oldImagePath = exitUser.getAvatar();
    String newAvatarPath = null;
    try {
      newAvatarPath = imageUploadService.uploadImage(updateUserDTO.getAvatarFile());

      exitUser.setFullName(updateUserDTO.getFullName());
      exitUser.setEmail(updateUserDTO.getEmail());
      exitUser.setAge(updateUserDTO.getAge());
      Optional.ofNullable(newAvatarPath).ifPresent(exitUser::setAvatar);
      // TODO: Implement password hashing later.
      Optional.ofNullable(updateUserDTO.getPassword()).ifPresent(exitUser::setPassword);

      Long roleId = updateUserDTO.getRoleId();
      if (roleId != null) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("role.not.found"));
        exitUser.setRole(role);
      }

      Long authProviderId = updateUserDTO.getAuthProviderId();
      if (authProviderId != null) {
        AuthProvider authProvider = authProviderRepository.findById(authProviderId)
            .orElseThrow(() -> new ResourceNotFoundException("auth_provider.not.found"));
        exitUser.setAuthProvider(authProvider);
      }

      exitUser = userRepository.save(exitUser);
      imageUploadService.deleteImage(oldImagePath, newAvatarPath != null);

      return modelMapper.map(exitUser, DetailUserDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newAvatarPath);
      throw e;
    }
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

    boolean existsActiveOrderForUser = orderRepository.existsActiveOrderForUser(id);
    if (existsActiveOrderForUser) {
      throw new ResourceNotFoundException("user.in.active.orders");
    }

    user.setDeletedAt(LocalDateTime.now());
    userRepository.save(user);
  }
}
