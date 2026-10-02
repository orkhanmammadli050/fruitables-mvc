package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.basket.BasketAddDto;
import az.edu.itbrains.fruitables.dtos.basket.BasketUpdateDto;
import az.edu.itbrains.fruitables.dtos.basket.BasketUserDto;
import az.edu.itbrains.fruitables.services.BasketService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BasketController {

    private final BasketService basketService;

    @PostMapping("/basket/update-quantity")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<String> updateQuantity(@RequestBody BasketUpdateDto dto, Principal principal) {
        try {
            String email = principal.getName();
            basketService.updateQuantity(dto, email);
            return ResponseEntity.ok("Say yeniləndi");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xəta baş verdi");
        }
    }


    @DeleteMapping("/basket/delete/{productId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<String> deleteBasketItem(@PathVariable Long productId, Principal principal) {
        try {
            String email = principal.getName();
            basketService.deleteByProductId(productId, email);
            return ResponseEntity.ok("Məhsul səbətdən silindi");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Silinmə zamanı xəta baş verdi: " + e.getMessage());
        }
    }

    @GetMapping("/basket")
    @PreAuthorize("isAuthenticated()")
    public String basket(Model model, Principal principal, HttpSession session) {
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

        return "basket/basket.html";
    }



    @PostMapping("/addToCart")
    @PreAuthorize("isAuthenticated()")
    public String addToCart(BasketAddDto basketAddDto, Principal principal){
        String email = principal.getName();
        basketService.createBasketItem(basketAddDto, email);
        return "redirect:/basket";
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam("code") String code,
                              Principal principal,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            BigDecimal discountPercent = basketService.applyCoupon(code, email);

            session.setAttribute("discountPercent", discountPercent);
            session.setAttribute("couponCode", code);

            redirectAttributes.addFlashAttribute("successMessage", "Kupon uğurla tətbiq olundu!");
            return "redirect:/basket";
        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/basket";
        }
    }
}
