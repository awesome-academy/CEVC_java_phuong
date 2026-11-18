package com.foodorder.foodapp.validation;

import java.util.Optional;

import com.foodorder.foodapp.model.User;
import com.foodorder.foodapp.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, EmailOwner> {
  private final UserRepository userRepository;

  @Override
  public boolean isValid(EmailOwner dto, ConstraintValidatorContext context) {
    if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
      return true;
    }
    Optional<User> user = userRepository.findByEmailIgnoreCase(dto.getEmail());
    boolean isUnique = user.isEmpty();

    // Editing a user who keeps their own email is not a duplicate
    if (isUnique || user.get().getId().equals(dto.getEditingUserId())) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("email")
        .addConstraintViolation();

    return false;
  }
}
