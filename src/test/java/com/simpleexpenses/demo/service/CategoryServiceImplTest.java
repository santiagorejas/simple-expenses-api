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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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

    private List<CategoryEntity> buildCategories() {
        CategoryEntity c1 = CategoryEntity.builder().Id(1L).categoryId("id1").title("t1").color("#ccc").userId(userId).build();
        CategoryEntity c2 = CategoryEntity.builder().Id(2L).categoryId("id2").title("t2").color("#ccc").userId(userId).build();
        CategoryEntity c3 = CategoryEntity.builder().Id(3L).categoryId("id3").title("t3").color("#ccc").userId(userId).build();

        List<CategoryEntity> categories = new ArrayList<>();
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);

        return categories;
    }

    @Test
    final void testGetCategories() {

        List<CategoryEntity> categories = this.buildCategories();

        when(this.categoryRepository.findAllByUserId(userId)).thenReturn(Optional.of(categories));

        List<CategoryDto> categoriesDtoList = this.categoryService.getCategories();

        Iterator<CategoryEntity> entityIterator = categories.iterator();
        Iterator<CategoryDto> dtoIterator = categoriesDtoList.iterator();

        assertEquals(categories.size(), categoriesDtoList.size());

        CategoryEntity currentCategoryEntity;
        CategoryDto currentCategoryDto;

        while (entityIterator.hasNext() && dtoIterator.hasNext()) {
            currentCategoryEntity = entityIterator.next();
            currentCategoryDto = dtoIterator.next();

            assertEquals(currentCategoryEntity.getId(), currentCategoryDto.getId());
            assertEquals(currentCategoryEntity.getCategoryId(), currentCategoryDto.getCategoryId());
            assertEquals(currentCategoryEntity.getTitle(), currentCategoryDto.getTitle());
            assertEquals(currentCategoryEntity.getColor(), currentCategoryDto.getColor());
            assertEquals(currentCategoryEntity.getUserId(), currentCategoryDto.getUserId());
        }
    }
}
