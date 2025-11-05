package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
public class CategoriesController {
  @GetMapping
  public String indexCategory() {
    return "admin/categories/index";
  }

  @GetMapping("/{id}")
  public String detailCategory() {
    return "admin/categories/detail";
  }

  @GetMapping("/new")
  public String newCategory() {
    return "admin/categories/new";
  }

  @GetMapping("/{id}/edit")
  public String editCategory() {
    return "admin/categories/edit";
  }
}
