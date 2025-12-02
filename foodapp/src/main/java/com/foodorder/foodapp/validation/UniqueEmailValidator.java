package com.foodorder.foodapp.validation;

import java.util.Optional;

import com.foodorder.foodapp.dto.user.ClientUpdateUserDTO;
import com.foodorder.foodapp.dto.user.UpdateUserDTO;
import com.foodorder.foodapp.dto.user.UserOperationDTO;
import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, UserOperationDTO> {
  private final UserRepository userRepository;

  @Override
  public boolean isValid(UserOperationDTO dto, ConstraintValidatorContext context) {
    if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
      return true;
    }
    Optional<User> user = userRepository.findByEmailIgnoreCase(dto.getEmail());
    boolean isUnique = user.isEmpty();

    if (isUnique)
      return true;

    if (dto instanceof UpdateUserDTO updateDto) {
      if (user.get().getId().equals(updateDto.getId())) {
        return true;
      }
    } else if (dto instanceof ClientUpdateUserDTO clientDto) {
      if (user.get().getId().equals(clientDto.getId())) {
        return true;
      }
    }

    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("email")
        .addConstraintViolation();

    return false;
  }
}
