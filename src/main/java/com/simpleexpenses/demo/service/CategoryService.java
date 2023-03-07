package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getCategories();

    CategoryDto updateCategory(String categoryId, CategoryDto categoryDto);
}
