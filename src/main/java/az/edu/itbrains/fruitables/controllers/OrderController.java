package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.basket.BasketUserDto;
import az.edu.itbrains.fruitables.dtos.order.OrderCreateDto;
import az.edu.itbrains.fruitables.repositories.OrderRepository;
import az.edu.itbrains.fruitables.services.BasketService;
import az.edu.itbrains.fruitables.services.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final BasketService basketService;

    @Autowired
    private final OrderRepository orderRepository;

    @GetMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String checkout(Model model, Principal principal, HttpSession session) {

        String email = principal.getName();
        List<BasketUserDto> basketItemUserDtoList = basketService.getBasketItems(email);


        BigDecimal subtotal = BigDecimal.ZERO;
        for (BasketUserDto item : basketItemUserDtoList) {
            if (item.getTotalPrice() != null) {
                subtotal = subtotal.add(item.getTotalPrice());
            }
        }

        BigDecimal discountPercent = (BigDecimal) session.getAttribute("discountPercent");
        BigDecimal discountedAmount = BigDecimal.ZERO;
        BigDecimal total = subtotal;

        if (discountPercent != null) {
            discountedAmount = subtotal.multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            total = subtotal.subtract(discountedAmount);
        }

        model.addAttribute("baskets", basketItemUserDtoList);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);
        model.addAttribute("discountedAmount", discountedAmount);

        return "order/checkout.html";
    }


    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String payment(OrderCreateDto orderCreateDto, Principal principal,HttpSession session) {
        String email = principal.getName();

        orderService.createOrder(orderCreateDto, email);

        String couponCode = (String) session.getAttribute("couponCode");
        if (couponCode != null) {
            basketService.markCouponAsUsed(couponCode, email);

            session.removeAttribute("discountPercent");
            session.removeAttribute("couponCode");
        }

        return "redirect:/success";
    }


    @GetMapping("/success")
    public String success() {
        return "order/success.html";
    }

    @GetMapping("/dashboard/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/order/index";
    }

    @GetMapping("/dashboard/order/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return "redirect:/dashboard/orders";
    }
}
