package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("isAuthenticated()")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("categories", this.categoryService.listAll());
        model.addAttribute("bodyContent", "category/categories");
        return "master-template";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("bodyContent", "category/categoriesCreate");
        return "master-template";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String name) {
        this.categoryService.create(name);
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model) {
        model.addAttribute("category", this.categoryService.findById(id));
        model.addAttribute("bodyContent", "category/categoriesCreate");
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editPost(@PathVariable Long id,
                           @RequestParam String name) {
        this.categoryService.update(id, name);
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
