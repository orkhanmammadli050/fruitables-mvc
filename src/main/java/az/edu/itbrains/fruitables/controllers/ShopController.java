package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.product.ProductDetailDto;
import az.edu.itbrains.fruitables.dtos.product.ProductFilterDto;
import az.edu.itbrains.fruitables.dtos.shop.ShopFilterDto;
import az.edu.itbrains.fruitables.models.Product;
import az.edu.itbrains.fruitables.services.CategoryService;
import az.edu.itbrains.fruitables.services.CommentService;
import az.edu.itbrains.fruitables.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;


    @GetMapping("/{category}/{slug}")
    public String detail(@PathVariable String slug, Model model) {

        ProductDetailDto productDetailDto = productService.getProductBySlug(slug);

        var comments = commentService.getCommentsByProductId(productDetailDto.getId());

        double averageRating = productService.calculateAverageRating(productDetailDto.getId());

        model.addAttribute("featuredProducts", productService.getFeatureProducts());
        model.addAttribute("product", productDetailDto);
        model.addAttribute("productComments", comments);
        model.addAttribute("averageRating", averageRating);

        return "shop/detail.html";
    }


    @PostMapping("/product/comment/add/{id}")
    public String addProductComment(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String content,
            @RequestParam int rating,
            @RequestParam String categorySlug,
            @RequestParam String productSlug) {

        commentService.addComment(id, name, email, content,rating);

        return "redirect:/" + categorySlug + "/" + productSlug;
    }


    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productService.getShopProducts(categoryId, keyword, minPrice, maxPrice, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());


        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);

        return "shop/shop.html";
    }



}
