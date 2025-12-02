package com.foodorder.foodapp.controller.web.admin;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;

import com.foodorder.foodapp.dto.setting.LanguageSettingsDTO;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/settings")
@AllArgsConstructor
public class SettingsController {

  private final LocaleResolver localeResolver;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model, HttpServletRequest request) {
    Locale currentLocale = localeResolver.resolveLocale(request);
    model.addAttribute("currentLocale", currentLocale.getLanguage());
  }

  @GetMapping
  public String settings() {
    return "admin/settings/setting";
  }

  @PostMapping("/language")
  public String updateLanguage(LanguageSettingsDTO request,
      @NonNull HttpServletRequest httpRequest, HttpServletResponse response) {
    String language = request.getLanguage();

    Locale locale;
    if ("vi".equalsIgnoreCase(language)) {
      locale = Locale.of("vi");
    } else {
      locale = Locale.ENGLISH;
    }
    localeResolver.setLocale(httpRequest, response, locale);

    return "redirect:/admin/settings";
  }
}
