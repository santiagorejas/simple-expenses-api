package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.CategoryDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.CategoryEntity;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private CategoryEntity buildCategoryEntity() {
        String categoryId = "categoryId";
        String title = "title";
        String color = "#ffffff";

        return CategoryEntity
                .builder()
                .categoryId(categoryId)
                .userId(userId)
                .title(title)
                .color(color)
                .build();
    }

    @Test
    final void testCreateCategory() {

        String title = "title";
        String color = "#ffffff";

        CategoryDto categoryDto = CategoryDto
                .builder()
                .title(title)
                .color(color)
                .build();

        CategoryEntity categoryEntity = this.buildCategoryEntity();

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

    @Test
    final void testUpdateCategory() {

        String categoryId = "categoryId";

        CategoryDto categoryDto = CategoryDto
                .builder()
                .title("New title")
                .color("#ccc")
                .build();

        CategoryEntity categoryEntity = this.buildCategoryEntity();

        CategoryEntity updatedCategoryEntity = CategoryEntity
                .builder()
                .Id(categoryEntity.getId())
                .categoryId(categoryEntity.getCategoryId())
                .title("New title")
                .color("#ccc")
                .build();

        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.of(categoryEntity));
        when(this.categoryRepository.save(categoryEntity))
                .thenReturn(updatedCategoryEntity);

        CategoryDto updatedCategoryDto = this.categoryService.updateCategory(categoryId, categoryDto);

        assertEquals(categoryDto.getTitle(), updatedCategoryDto.getTitle());
        assertEquals(categoryDto.getColor(), updatedCategoryDto.getColor());
    }

    @Test
    final void testUpdateCategory_EntityNotFoundException() {

        String categoryId = "categoryId";

        CategoryDto categoryDto = CategoryDto
                .builder()
                .title("New title")
                .color("#ccc")
                .build();


        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.categoryService.updateCategory(categoryId, categoryDto);
        });

        verify(this.categoryRepository, never()).save(any(CategoryEntity.class));
    }

    @Test
    final void testUpdateCategory_AccessDeniedException() {

        String categoryId = "categoryId";

        CategoryDto categoryDto = CategoryDto
                .builder()
                .title("New title")
                .color("#ccc")
                .build();

        CategoryEntity categoryEntity = this.buildCategoryEntity();
        categoryEntity.setUserId("another" + userId);

        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        assertThrows(AccessDeniedException.class, () -> {
            this.categoryService.updateCategory(categoryId, categoryDto);
        });

        verify(this.categoryRepository, never()).save(any(CategoryEntity.class));
    }

    @Test
    final void testDeleteCategory() {

        String categoryId = "categoryId";

        CategoryEntity categoryEntity = this.buildCategoryEntity();
        Set<CategoryEntity> categories = new HashSet<>();
        categories.add(categoryEntity);

        ExpenseEntity exp1 = ExpenseEntity
                .builder()
                .id(1L).expenseId("1").title("t1").amount(new BigDecimal(1)).categories(categories)
                .build();
        ExpenseEntity exp2 = ExpenseEntity
                .builder().
                id(2L).expenseId("2").title("t2").amount(new BigDecimal(2)).categories(categories)
                .build();
        ExpenseEntity exp3 = ExpenseEntity
                .builder()
                .id(3L).expenseId("3").title("t3").amount(new BigDecimal(3)).categories(categories)
                .build();

        List<ExpenseEntity> expenses = new ArrayList<>();
        expenses.add(exp1);
        expenses.add(exp2);
        expenses.add(exp3);

        categoryEntity.setExpenses(new HashSet<>(expenses));

        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        this.categoryService.deleteCategory(categoryId);

        for (ExpenseEntity exp: expenses) {
            assertEquals(exp.getCategories().size(), 0);
        }

        verify(this.expenseRepository, times(expenses.size())).save(any(ExpenseEntity.class));
        verify(this.categoryRepository, times(1)).delete(categoryEntity);

    }

    @Test
    final void testDeleteCategory_EntityNotFoundException() {

        String categoryId = "categoryId";

        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.categoryService.deleteCategory(categoryId);
        });

        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));
        verify(this.categoryRepository, never()).delete(any(CategoryEntity.class));

    }

    @Test
    final void testDeleteCategory_AccessDeniedException() {

        String categoryId = "categoryId";

        CategoryEntity categoryEntity = this.buildCategoryEntity();
        categoryEntity.setUserId("another" + userId);

        when(this.categoryRepository.findByCategoryId(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        assertThrows(AccessDeniedException.class, () -> {
            this.categoryService.deleteCategory(categoryId);
        });

        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));
        verify(this.categoryRepository, never()).delete(categoryEntity);

    }
}
