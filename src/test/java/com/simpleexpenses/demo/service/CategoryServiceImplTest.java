package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.CategoryDto;
import com.simpleexpenses.demo.model.entity.CategoryEntity;
import com.simpleexpenses.demo.repository.CategoryRepository;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ExpenseRepository expenseRepository;

    private final String userId = "userId";

    @BeforeEach
    void setUp() {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @Test
    final void testCreateCategory() {

        String categoryId = "categoryId";
        String title = "title";
        String color = "#ffffff";

        CategoryDto categoryDto = CategoryDto
                .builder()
                .title(title)
                .color(color)
                .build();

        CategoryEntity categoryEntity = CategoryEntity
                .builder()
                .categoryId(categoryId)
                .userId(userId)
                .title(title)
                .color(color)
                .build();

        when(this.categoryRepository.save(any(CategoryEntity.class)))
                .thenReturn(categoryEntity);

        CategoryDto createdCategoryDto = this.categoryService.createCategory(categoryDto);

        assertEquals(categoryDto.getTitle(), createdCategoryDto.getTitle());
        assertEquals(categoryDto.getColor(), createdCategoryDto.getColor());
        assertEquals(userId, createdCategoryDto.getUserId());
    }

}
