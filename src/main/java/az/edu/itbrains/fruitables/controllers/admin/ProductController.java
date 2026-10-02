package az.edu.itbrains.fruitables.controllers.admin;

import az.edu.itbrains.fruitables.dtos.category.CategoryDto;
import az.edu.itbrains.fruitables.dtos.product.ProductCreateDto;
import az.edu.itbrains.fruitables.dtos.product.ProductDashboardDto;
import az.edu.itbrains.fruitables.dtos.product.ProductUpdateDto;
import az.edu.itbrains.fruitables.services.CategoryService;
import az.edu.itbrains.fruitables.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;


    @GetMapping("/products")
    public String getAll(Model model) {
        List<ProductDashboardDto> productDashboardDtoList = productService.getDashboardProducts();
        model.addAttribute("products", productDashboardDtoList);
        return "admin/product/index.html";
    }

    @GetMapping("/product/create")
    public String create(Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategories();
        model.addAttribute("categories", categoryDtoList);

        return "admin/product/create.html";
    }

    @PostMapping("/product/create")
    public String create(ProductCreateDto productCreate) {

        productService.createProduct(productCreate);

        return "redirect:/dashboard/products";
    }



    @GetMapping("/product/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategories();
        model.addAttribute("categories", categoryDtoList);
        ProductUpdateDto productUpdateDto = productService.getUpdatedProduct(id);
        model.addAttribute("product", productUpdateDto);
        return "admin/product/update.html";
    }

    @PostMapping("/product/update/{id}")
    public String update(@PathVariable Long id, ProductUpdateDto productUpdate) {

        productService.updateProduct(id,productUpdate);

        return "redirect:/dashboard/products";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/dashboard/products";
    }
}
