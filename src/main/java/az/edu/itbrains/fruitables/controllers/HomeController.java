package az.edu.itbrains.fruitables.controllers;

import az.edu.itbrains.fruitables.dtos.category.CategoryPinnedDto;
import az.edu.itbrains.fruitables.dtos.product.ProductBestsellerDto;
import az.edu.itbrains.fruitables.dtos.product.ProductFeatureDto;
import az.edu.itbrains.fruitables.dtos.product.ProductPinnedDto;
import az.edu.itbrains.fruitables.services.CategoryService;
import az.edu.itbrains.fruitables.services.CommentService;
import az.edu.itbrains.fruitables.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;


    @GetMapping
    public String home(Model model) {

        List<CategoryPinnedDto> categoryPinnedDtoList = categoryService.getPinnedCategories();
        List<ProductPinnedDto> productPinnedDtoList = productService.getPinnedProducts();
        List<ProductFeatureDto> productFeatureDtoList = productService.getFeatureProducts();
        List<ProductBestsellerDto> bestsellers = productService.getBestsellerProducts();


        model.addAttribute("bestsellers", bestsellers);
        model.addAttribute("pinnedCategories", categoryPinnedDtoList);
        model.addAttribute("pinnedProducts", productPinnedDtoList);
        model.addAttribute("featuredProducts", productFeatureDtoList);

        model.addAttribute("testimonials", commentService.getAllComments());
        return "index.html";
    }


    @PostMapping("/comment/add")
    public String addComment(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String content) {

        commentService.addComment(name, email, content);
        return "redirect:/";
    }
}
