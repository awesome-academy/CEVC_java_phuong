package com.foodorder.foodapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import lombok.AllArgsConstructor;

/**
 * Error handling for admin web pages: renders an HTML error page instead of JSON.
 * API errors are handled by {@link GlobalExceptionHandler}.
 */
@ControllerAdvice(basePackages = "com.foodorder.foodapp.controller.web")
@AllArgsConstructor
public class AdminExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(AdminExceptionHandler.class);

  private final MessageSource messageSource;

  /** Builds the shared admin error page. */
  public static ModelAndView errorPage(HttpStatus status, String title, String message) {
    ModelAndView mav = new ModelAndView("admin/error/error", status);
    mav.addObject("status", status.value());
    mav.addObject("title", title);
    mav.addObject("message", message);
    return mav;
  }

  public static ModelAndView notFoundPage() {
    return errorPage(HttpStatus.NOT_FOUND, "Page not found",
        "The page you are looking for does not exist or has been removed.");
  }

  // Unknown id (e.g. /admin/suggestions/9999) or non-numeric id (e.g. /admin/suggestions/abc)
  @ExceptionHandler({ ResourceNotFoundException.class, MethodArgumentTypeMismatchException.class })
  public ModelAndView handleNotFound() {
    return notFoundPage();
  }

  @ExceptionHandler(BadRequestException.class)
  public ModelAndView handleBadRequest(BadRequestException ex) {
    String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(),
        LocaleContextHolder.getLocale());
    return errorPage(HttpStatus.BAD_REQUEST, "Bad request", message);
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleUnexpected(Exception ex) {
    logger.error("Unhandled admin exception: ", ex);
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong",
        "An unexpected error occurred. Please try again later.");
  }
}
