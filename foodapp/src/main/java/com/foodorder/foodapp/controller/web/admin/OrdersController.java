package com.foodorder.foodapp.controller.web.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodorder.foodapp.dto.order.AdminDetailOrderDTO;
import com.foodorder.foodapp.dto.order.AdminSearchOrderDTO;
import com.foodorder.foodapp.dto.order.AdminUpdateOrderStatusDTO;
import com.foodorder.foodapp.dto.order.ListOrderDTO;
import com.foodorder.foodapp.dto.order_status.OrderStatusDTO;
import com.foodorder.foodapp.enums.MessageCode;
import com.foodorder.foodapp.service.OrderService;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/orders")
@AllArgsConstructor
public class OrdersController {
  private final OrderService orderService;

  @ModelAttribute
  private void addSelectOptionsToModel(Model model) {
    List<OrderStatusDTO> orderStatuses = orderService.getAllOrderStatuses();
    model.addAttribute("orderStatuses", orderStatuses);
  }

  @GetMapping
  public String indexOrder(
      @RequestParam(required = false) String productName,
      @RequestParam(required = false) Long orderStatusId,
      @RequestParam(required = false) Long priceLevel,
      @RequestParam(defaultValue = "${app.pagination.default-page}") int page,
      @RequestParam(defaultValue = "${app.pagination.default-per-page}") int perPage,
      Model model) {
    AdminSearchOrderDTO params = new AdminSearchOrderDTO(productName, orderStatusId, priceLevel, page, perPage);
    Page<ListOrderDTO> orders = orderService.getAllOrders(params);
    model.addAttribute("search", params);
    model.addAttribute("orders", orders);
    model.addAttribute("totalPages", orders.getTotalPages());

    return "admin/orders/index";
  }

  @GetMapping("/{id}")
  public String detailProduct(@NonNull @PathVariable Long id, Model model) {
    AdminDetailOrderDTO order = orderService.getOrderById(id);
    model.addAttribute("order", order);

    return "admin/orders/detail";

  }

  @PostMapping("/{id}/status")
  public String updateOrderStatus(
      @NonNull @PathVariable Long id,
      @Valid @ModelAttribute("order") AdminUpdateOrderStatusDTO updateOrderStatusDTO,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (bindingResult.hasErrors()) {
      AdminSearchOrderDTO params = new AdminSearchOrderDTO();
      Page<ListOrderDTO> orders = orderService.getAllOrders(params);
      model.addAttribute("orders", orders);
      model.addAttribute("totalPages", orders.getTotalPages());
      model.addAttribute("search", params);

      model.addAttribute("failedMessage", MessageCode.UPDATE_FAILED.getCode());
      return "admin/orders/index";
    }

    orderService.updateOrderStatus(id, updateOrderStatusDTO);
    redirectAttributes.addFlashAttribute("successMessage",
        MessageCode.UPDATE_SUCCESS.getCode());

    return "redirect:/admin/orders";
  }

}
