package com.simpleexpenses.demo.controller;

import com.simpleexpenses.demo.dto.CategoryDto;
import com.simpleexpenses.demo.model.request.CategoryRequest;
import com.simpleexpenses.demo.model.response.CategoryResponse;
import com.simpleexpenses.demo.model.response.MessageResponse;
import com.simpleexpenses.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

        List<CategoryDto> categoriesDto = this.categoryService.getCategories();

        ModelMapper modelMapper = new ModelMapper();
        List<CategoryResponse> categoriesResponse = categoriesDto
                .stream()
                .map(categoryDto -> modelMapper.map(categoryDto, CategoryResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoriesResponse);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity updateCategory(
            @PathVariable String categoryId,
            @RequestBody CategoryRequest category) {

        ModelMapper modelMapper = new ModelMapper();
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);

        CategoryDto updatedCategoryDto = this.categoryService.updateCategory(categoryId, categoryDto);
        CategoryResponse updatedCategoryResponse = modelMapper.map(updatedCategoryDto, CategoryResponse.class);

        return ResponseEntity.ok(updatedCategoryResponse);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable String categoryId) {

        this.categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
    }



}
