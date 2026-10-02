package az.edu.itbrains.fruitables.controllers.admin;

import az.edu.itbrains.fruitables.dtos.category.CategoryCreateDto;
import az.edu.itbrains.fruitables.dtos.category.CategoryDashboardDto;
import az.edu.itbrains.fruitables.dtos.category.CategoryUpdateDto;
import az.edu.itbrains.fruitables.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String getAll(Model model) {
        List<CategoryDashboardDto> categoryDashboardDtoList = categoryService.getDashboardCategories();
        model.addAttribute("categories", categoryDashboardDtoList);
        return "admin/category/index.html";
    }


    @GetMapping("/category/create")
    public String create() {

        return "admin/category/create.html";
    }

    @PostMapping("/category/create")
    public String create(CategoryCreateDto categoryCreate) {

        categoryService.createCategory(categoryCreate);

        return "redirect:/dashboard/categories";
    }

    @GetMapping("/category/update/{id}")
    public String update(@PathVariable Long id, Model model) {
        CategoryUpdateDto categoryUpdateDto = categoryService.getUpdatedCategory(id);
        model.addAttribute("category", categoryUpdateDto);
        return "admin/category/update.html";
    }

    @PostMapping("/category/update/{id}")
    public String update(@PathVariable Long id, CategoryUpdateDto categoryUpdate) {

        categoryService.updateCategory(id,categoryUpdate);

        return "redirect:/dashboard/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/dashboard/categories";
    }
}
