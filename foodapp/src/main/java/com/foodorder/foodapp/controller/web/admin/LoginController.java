package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/login")
@AllArgsConstructor
public class LoginController {
  @RequestMapping
  public String login() {
    return "admin/authentication/login";
  }
}
