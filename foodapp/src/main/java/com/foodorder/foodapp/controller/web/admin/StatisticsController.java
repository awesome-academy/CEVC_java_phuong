package com.foodorder.foodapp.controller.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {

  @GetMapping
  public String statistics() {
    return "admin/statistics/index";
  }
}
