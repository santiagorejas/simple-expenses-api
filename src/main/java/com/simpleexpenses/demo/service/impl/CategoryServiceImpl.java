package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.CategoryDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.CategoryEntity;
import com.simpleexpenses.demo.repository.CategoryRepository;
import com.simpleexpenses.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        ModelMapper modelMapper = new ModelMapper();
        CategoryEntity categoryEntity = modelMapper.map(categoryDto, CategoryEntity.class);
        categoryEntity.setUserId(userId);
        categoryEntity.setCategoryId(UUID.randomUUID().toString());

        CategoryEntity storedCategoryEntity = this.categoryRepository.save(categoryEntity);
        CategoryDto storedCategoryDto = modelMapper.map(storedCategoryEntity, CategoryDto.class);

        return storedCategoryDto;
    }

    @Override
    public List<CategoryDto> getCategories() {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<CategoryEntity> categories = this.categoryRepository
                .findAllByUserId(userId)
                .orElse(new ArrayList<CategoryEntity>());

        ModelMapper modelMapper = new ModelMapper();
        List<CategoryDto> categoriesDto =  categories
                .stream()
                .map(categoryEntity -> modelMapper.map(categoryEntity, CategoryDto.class))
                .collect(Collectors.toList());

        return categoriesDto;
    }

    @Override
    public CategoryDto updateCategory(String categoryId, CategoryDto categoryDto) {

        CategoryEntity categoryEntity = this.categoryRepository
                .findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category doesn't exist."));

        String userId =  SecurityContextHolder.getContext().getAuthentication().getName();

        if (!userId.equals(categoryEntity.getUserId())) {
            throw new AccessDeniedException("You don't own this category.");
        }

        categoryEntity.setTitle(categoryDto.getTitle());
        categoryEntity.setColor(categoryDto.getColor());

        CategoryEntity updatedCategoryEntity = this.categoryRepository.save(categoryEntity);

        ModelMapper modelMapper = new ModelMapper();
        CategoryDto updatedCategoryDto = modelMapper.map(updatedCategoryEntity, CategoryDto.class);

        return updatedCategoryDto;
    }

    @Override
    public void deleteCategory(String categoryId) {

        CategoryEntity categoryEntity = this.categoryRepository
                .findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!userId.equals(categoryEntity.getUserId())) {
            throw new AccessDeniedException("You don't own this category");
        }

        this.categoryRepository.delete(categoryEntity);
    }
}
