package com.foodorder.foodapp.controller.web.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.config.PaginationConfig;
import com.foodorder.foodapp.dto.auth_provider.AuthProviderDTO;
import com.foodorder.foodapp.dto.role.RoleDTO;
import com.foodorder.foodapp.dto.user.CreateUserDTO;
import com.foodorder.foodapp.dto.user.DetailUserDTO;
import com.foodorder.foodapp.dto.user.ListUserDTO;
import com.foodorder.foodapp.dto.user.SearchUserDTO;
import com.foodorder.foodapp.dto.user.UpdateUserDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.exception.BadRequestException;
import com.foodorder.foodapp.exception.ResourceNotFoundException;
import com.foodorder.foodapp.service.UserService;

import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@AllArgsConstructor
public class UsersController {
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final PaginationConfig paginationConfig;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model) {
    List<AuthProviderDTO> authProviders = userService.getAllAuthProviders();
    List<RoleDTO> roles = userService.getAllRoles();
    model.addAttribute("authProviders", authProviders);
    model.addAttribute("roles", roles);
  }

  @GetMapping
  public String indexUser(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long roleId,
      @RequestParam(defaultValue = "${app.pagination.default-page}") int page,
      @RequestParam(defaultValue = "${app.pagination.default-per-page}") int perPage,
      Model model) {
    SearchUserDTO params = new SearchUserDTO(name, roleId, page, perPage, paginationConfig);
    Page<ListUserDTO> users = userService.getAllUsers(params);

    model.addAttribute("search", params);
    model.addAttribute("users", users);
    model.addAttribute("totalPages", users.getTotalPages());

    return "admin/users/index";
  }

  @GetMapping("/{id}")
  public String detailUser(@NonNull @PathVariable Long id, Model model,
      RedirectAttributes redirectAttributes) {
    try {
      DetailUserDTO user = userService.getUserById(id);
      model.addAttribute("user", user);

      return "admin/users/detail";
    } catch (ResourceNotFoundException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/users";
    }
  }

  @GetMapping("/new")
  public String newUser(Model model) {
    model.addAttribute("user", new CreateUserDTO());

    return "admin/users/new";
  }

  @PostMapping("/new")
  public String createUser(
      @Valid @ModelAttribute("user") CreateUserDTO createUserDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("failedMessage", MessageCode.CREATE_FAILED.getCode());
      return "admin/users/new";
    }
    userService.createUser(createUserDTO);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.CREATE_SUCCESS.getCode());

    return "redirect:/admin/users";
  }

  @GetMapping("/{id}/edit")
  public String editUser(@NonNull @PathVariable Long id, Model model) {
    DetailUserDTO user = userService.getUserById(id);
    UpdateUserDTO updateUserDTO = modelMapper.map(user,
        UpdateUserDTO.class);

    if (user.getRole() != null) {
      updateUserDTO.setRoleId(user.getRole().getId());
    }
    if (user.getAuthProvider() != null) {
      updateUserDTO.setAuthProviderId(user.getAuthProvider().getId());
    }

    model.addAttribute("user", updateUserDTO);

    return "admin/users/edit";
  }

  @PostMapping("/{id}/edit")
  public String updateUser(
      @PathVariable Long id,
      @Valid @ModelAttribute("user") UpdateUserDTO updateUserDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("failedMessage", MessageCode.UPDATE_FAILED.getCode());

      return "admin/users/edit";
    }

    userService.updateUser(updateUserDTO);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/users";
  }

  @GetMapping("/{id}/delete")
  public String deleteUser(
      @PathVariable Long id,
      RedirectAttributes redirectAttributes,
      Model model) {
    try {
      userService.deleteUser(id);
      redirectAttributes.addFlashAttribute("successMessage",
          MessageCode.DELETE_SUCCESS.getCode());

      return "redirect:/admin/users";
    } catch (BadRequestException e) {
      redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());

      return "redirect:/admin/users";
    }
  }
}
