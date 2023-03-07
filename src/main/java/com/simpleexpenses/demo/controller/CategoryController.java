package com.simpleexpenses.demo.controller;

import com.simpleexpenses.demo.dto.CategoryDto;
import com.simpleexpenses.demo.model.request.CategoryRequest;
import com.simpleexpenses.demo.model.response.CategoryResponse;
import com.simpleexpenses.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryRequest category) {

        ModelMapper modelMapper = new ModelMapper();
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);

        CategoryDto createdCategoryDto = this.categoryService.createCategory(categoryDto);

        CategoryResponse createdCategoryResponse = modelMapper.map(createdCategoryDto, CategoryResponse.class);

        return ResponseEntity.ok(createdCategoryResponse);
    }

    @GetMapping
    public ResponseEntity getCategories() {
        return null;
    }

    @PutMapping
    public ResponseEntity updateCategory() {
        return null;
    }

    @DeleteMapping
    public ResponseEntity deleteCategory() {
        return null;
    }



}
