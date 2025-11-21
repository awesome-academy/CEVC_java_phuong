package com.foodorder.foodapp.dto.user;

import org.springframework.web.multipart.MultipartFile;

import com.foodorder.foodapp.validation.FileSize;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOperationDTO {
  @NotBlank(message = "{validation.user.full_name.required}")
  @Size(max = 100, message = "{validation.user.full_name.max}")
  private String fullName;

  @NotBlank(message = "{validation.user.email.required}")
  @Size(max = 255, message = "{validation.user.email.max}")
  @Email(message = "{validation.user.email.format}")
  private String email;

  @Min(value = 0, message = "{validation.user.age.min}")
  private Integer age;

  @FileSize(max = 2 * 1024 * 1024, message = "{validation.image.size}")
  private MultipartFile avatarFile;
}
