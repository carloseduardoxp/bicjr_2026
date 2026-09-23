package com.openisle.controller;

import com.openisle.model.Category;
import com.openisle.service.CategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@RequestBody CategoryRequest req) {
        Category c = categoryService.createCategory(req.getName());
        return toDto(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.listCategories().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDto get(@PathVariable Long id) {
        return toDto(categoryService.getCategory(id));
    }

    private CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        return dto;
    }

    @Data
    private static class CategoryRequest {
        private String name;
    }

    @Data
    private static class CategoryDto {
        private Long id;
        private String name;
    }
}
