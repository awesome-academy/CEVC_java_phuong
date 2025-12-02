package com.foodorder.foodapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;

import com.foodorder.foodapp.dto.user.ClientDetailUserDTO;
import com.foodorder.foodapp.dto.user.ClientUpdateUserDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;

@Service
@AllArgsConstructor
public class ClientUserService {
  private final ModelMapper modelMapper;
  private final ImageUploadService imageUploadService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public ClientDetailUserDTO getUserById(User currentUser) {
    return modelMapper.map(currentUser, ClientDetailUserDTO.class);
  }

  public ClientDetailUserDTO updateUser(User user, ClientUpdateUserDTO updateUserDTO) {
    String oldImagePath = user.getAvatar();
    String newAvatarPath = null;
    try {
      newAvatarPath = imageUploadService.uploadImage(updateUserDTO.getAvatarFile());

      user.setFullName(updateUserDTO.getFullName());
      user.setEmail(updateUserDTO.getEmail());
      user.setAge(updateUserDTO.getAge());
      Optional.ofNullable(newAvatarPath).ifPresent(user::setAvatar);
      Optional.ofNullable(updateUserDTO.getPassword())
          .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

      userRepository.save(user);
      imageUploadService.deleteImage(oldImagePath, newAvatarPath != null);

      return modelMapper.map(user, ClientDetailUserDTO.class);
    } catch (Exception e) {
      imageUploadService.deleteImage(newAvatarPath);
      throw e;
    }
  }
}
