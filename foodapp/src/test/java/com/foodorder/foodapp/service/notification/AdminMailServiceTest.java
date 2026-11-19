package com.foodorder.foodapp.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.foodorder.foodapp.config.properties.NotificationProperties;
import com.foodorder.foodapp.dto.statistics.MonthlyOrderReport;
import com.foodorder.foodapp.repository.UserRepository;
import com.foodorder.foodapp.utils.MoneyFormat;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/**
 * Renders the real mail templates (layout, @money bean) with Thymeleaf, without SMTP or database:
 * a template error shows up here instead of only in the logs of a running app.
 */
class AdminMailServiceTest {
  private final JavaMailSender mailSender = mock(JavaMailSender.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private SpringTemplateEngine templateEngine;
  private StaticApplicationContext applicationContext;

  @BeforeEach
  void setUp() {
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode(TemplateMode.HTML);
    resolver.setCharacterEncoding("UTF-8");
    templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(resolver);

    applicationContext = new StaticApplicationContext();
    applicationContext.registerSingleton("money", MoneyFormat.class);
    applicationContext.refresh();

    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    when(userRepository.findActiveAdminEmails()).thenReturn(List.of("admin01@example.com", "admin02@example.com"));
  }

  @Test
  void sendOrderPlaced_rendersOrderInLayoutToAllAdmins() throws Exception {
    service(true).sendOrderPlaced(new OrderPlacedSummary(15L, "Nguyễn Văn A", "a@example.com",
        LocalDateTime.of(2026, 11, 19, 17, 30), 95_000, "Giao trước 12h", "Nguyễn Văn A",
        "0901234567", "1 Lê Lợi, Quận 1, Hồ Chí Minh",
        List.of(new OrderPlacedSummary.Line("Phở bò", 2, 45_000))));

    MimeMessage message = sentMessage();
    assertThat(message.getSubject()).isEqualTo("New order #15");
    assertThat(message.getAllRecipients()).hasSize(2);
    assertThat((String) message.getContent())
        .contains("<title>New order #15</title>", "Phở bò", "90.000 ₫", "95.000 ₫",
            "Giao trước 12h", "Sent automatically by Foodapp");
  }

  @Test
  void sendMonthlyReport_rendersStatusesAndTopProducts() throws Exception {
    service(true).sendMonthlyReport(new MonthlyOrderReport(YearMonth.of(2026, 10), 3, 450_000,
        List.of(new MonthlyOrderReport.OrderStatusCount("COMPLETED", 3L, 450_000L)),
        List.of(new MonthlyOrderReport.TopProduct(8L, "Bún chả Hà Nội", 6L, 273_000L))));

    String html = (String) sentMessage().getContent();
    assertThat(html).contains("Order report 10/2026", "450.000 ₫", "COMPLETED", "Bún chả Hà Nội",
        "273.000 ₫", "Sent automatically by Foodapp");
  }

  @Test
  void send_mailDisabled_sendsNothing() {
    service(false).sendMonthlyReport(new MonthlyOrderReport(YearMonth.of(2026, 10), 0, 0, List.of(), List.of()));

    verify(mailSender, never()).send(any(MimeMessage.class));
  }

  private AdminMailService service(boolean mailEnabled) {
    NotificationProperties properties = new NotificationProperties(
        new NotificationProperties.Chatwork("https://api.chatwork.com/v2", "", ""),
        new NotificationProperties.Mail(mailEnabled, "foodapp@localhost"), "Asia/Ho_Chi_Minh");
    return new AdminMailService(mailSender, templateEngine, userRepository, properties, applicationContext);
  }

  private MimeMessage sentMessage() {
    ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(captor.capture());
    return captor.getValue();
  }
}
