package com.foodorder.foodapp.service.notification;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import com.foodorder.foodapp.config.properties.NotificationProperties;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HTML emails to every active admin user. Bodies are Thymeleaf templates under templates/mail/.
 * Does nothing (logged) while app.notification.mail.enabled is false.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AdminMailService {
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;
  private final UserRepository userRepository;
  private final NotificationProperties properties;
  private final ApplicationContext applicationContext;

  public void sendOrderPlaced(OrderPlacedSummary order) {
    Context context = templateContext();
    context.setVariable("order", order);
    send("New order #" + order.orderId(), "mail/order-placed", context);
  }

  public void sendMonthlyReport(MonthlyOrderReport report) {
    Context context = templateContext();
    context.setVariable("report", report);
    send("Order report " + report.month(), "mail/monthly-order-report", context);
  }

  /**
   * Emails are rendered outside a web request, so the context must carry the Spring
   * ApplicationContext itself for templates to reach beans (e.g. ${@money.vnd(...)}).
   */
  private Context templateContext() {
    Context context = new Context();
    context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
        new ThymeleafEvaluationContext(applicationContext, null));
    return context;
  }

  private void send(String subject, String template, Context context) {
    if (!properties.mail().enabled()) {
      log.info("Admin email disabled (MAIL_ENABLED): '{}' skipped", subject);
      return;
    }

    List<String> admins = userRepository.findActiveAdminEmails();
    if (admins.isEmpty()) {
      log.warn("No active admin user to email: '{}' skipped", subject);
      return;
    }

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
      helper.setFrom(properties.mail().from());
      helper.setTo(admins.toArray(String[]::new));
      helper.setSubject(subject);
      helper.setText(templateEngine.process(template, context), true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new IllegalStateException("Could not build email '" + subject + "'", e);
    }
  }
}
