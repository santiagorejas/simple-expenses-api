package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.CategoryEntity;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.CategoryRepository;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.impl.ExpenseServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

    @InjectMocks
    ExpenseServiceImpl expenseService;

    @Mock
    ExpensesGroupRepository expensesGroupRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    CategoryRepository categoryRepository;

    private final String userId = "userId";
    private BigDecimal amount = new BigDecimal(10);
    private Date date = new Date();

    @BeforeEach
    void setUp() {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    private ExpenseDto buildExpenseDto() {

        return ExpenseDto
                .builder()
                .title("title")
                .description("description")
                .amount(amount)
                .date(date)
                .expensesGroupId("expenseGroupId")
                .build();
    }

    private List<CategoryEntity> buildCategories() {
        CategoryEntity cat1 = CategoryEntity
                .builder()
                .Id(1)
                .categoryId("1")
                .title("Category 1")
                .color("#fff")
                .userId(userId)
                .build();
        CategoryEntity cat2 = CategoryEntity
                .builder()
                .Id(1)
                .categoryId("2")
                .title("Category 2")
                .color("#000")
                .userId(userId)
                .build();
        CategoryEntity cat3 = CategoryEntity
                .builder()
                .Id(1)
                .categoryId("3")
                .title("Category 3")
                .color("#eeefff")
                .userId(userId)
                .build();

        List<CategoryEntity> categories = new ArrayList<>();
        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);

        return categories;
    }

    private ExpenseEntity buildExpenseEntity() {
        return ExpenseEntity
                .builder()
                .expenseId("expenseId")
                .title("title")
                .description("description")
                .amount(amount)
                .date(date)
                .build();
    }

    private ExpensesGroupEntity buildExpensesGroupEntity() {

        return ExpensesGroupEntity
                .builder()
                .expensesGroupId("expenseGroupId")
                .title("An expenses group.")
                .description("This is a group expenses.")
                .userId(userId)
                .build();
    }

    @Test
    final void testCreateExpense() {

        ExpenseDto expenseDto = this.buildExpenseDto();

        List<String> categoriesId = new ArrayList<>();
        categoriesId.add("1");
        categoriesId.add("2");
        categoriesId.add("3");

        expenseDto.setCategoriesId(categoriesId);

        List<CategoryEntity> categories = this.buildCategories();

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();

        ExpenseEntity expenseEntity = this.buildExpenseEntity();
        expenseEntity.setExpensesGroup(expensesGroupEntity);
        expenseEntity.setCategories(new HashSet<>(categories));

        when(this.expensesGroupRepository.findByExpensesGroupId(expenseDto.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));
        when(this.categoryRepository.findAllByCategoryIdIn(expenseDto.getCategoriesId()))
                .thenReturn(Optional.of(categories));
        when(this.expenseRepository.save(any(ExpenseEntity.class)))
                .thenReturn(expenseEntity);

        ExpenseDto createdExpenseDto = this.expenseService.createExpense(expenseDto);

        assertEquals(expenseDto.getTitle(), createdExpenseDto.getTitle());
        assertEquals(expenseDto.getDescription(), createdExpenseDto.getDescription());
        assertEquals(expenseDto.getAmount(), createdExpenseDto.getAmount());
        assertEquals(expenseDto.getDate(), createdExpenseDto.getDate());
        assertEquals(expenseDto.getExpensesGroupId(), createdExpenseDto.getExpensesGroup().getExpensesGroupId());
        assertEquals(expenseDto.getCategoriesId().size(), createdExpenseDto.getCategories().size());
    }

    @Test
    final void testCreateExpense_EntityNotFoundException_ExpensesGroup() {

        ExpenseDto expenseDto = this.buildExpenseDto();

        when(this.expensesGroupRepository.findByExpensesGroupId(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.expenseService.createExpense(expenseDto);
        });
    }

    @Test
    final void testCreateExpense_AccessDeniedException_ExpensesGroup() {

        ExpenseDto expenseDto = this.buildExpenseDto();

        expenseDto.setCategoriesId(new ArrayList<>());

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();
        expensesGroupEntity.setUserId("another" + userId);

        when(this.expensesGroupRepository.findByExpensesGroupId(expenseDto.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));

        assertThrows(AccessDeniedException.class, () -> {
           this.expenseService.createExpense(expenseDto);
        });


        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));
    }

    @Test
    final void testCreateExpense_EntityNotFoundException_Category() {

        ExpenseDto expenseDto = this.buildExpenseDto();

        List<String> categoriesId = new ArrayList<>();
        categoriesId.add("1");
        categoriesId.add("2");
        categoriesId.add("3");

        expenseDto.setCategoriesId(categoriesId);

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();

        when(this.expensesGroupRepository.findByExpensesGroupId(expenseDto.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));
        when(this.categoryRepository.findAllByCategoryIdIn(expenseDto.getCategoriesId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.expenseService.createExpense(expenseDto);
        });

        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));
    }

    @Test
    final void testCreateExpense_AccessDeniedException_Categories() {

        ExpenseDto expenseDto = this.buildExpenseDto();

        List<String> categoriesId = new ArrayList<>();
        categoriesId.add("1");
        categoriesId.add("2");
        categoriesId.add("3");

        expenseDto.setCategoriesId(categoriesId);

        List<CategoryEntity> categories = this.buildCategories();
        categories.get(0).setUserId("another" + userId);

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();

        when(this.expensesGroupRepository.findByExpensesGroupId(expenseDto.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));
        when(this.categoryRepository.findAllByCategoryIdIn(expenseDto.getCategoriesId()))
                .thenReturn(Optional.of(categories));


        assertThrows(AccessDeniedException.class, () -> {
            this.expenseService.createExpense(expenseDto);
        });

        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));

    }

    @Test
    final void testUpdateExpense() {

        String expenseId = "expenseId";

        List<CategoryEntity> categories = this.buildCategories();

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();

        ExpenseEntity expenseEntity = this.buildExpenseEntity();
        expenseEntity.setExpensesGroup(expensesGroupEntity);
        expenseEntity.setCategories(new HashSet<>(categories));

        String newTitle = "New title";
        String newDescription = "New description";
        BigDecimal newAmount = new BigDecimal(20);

        ExpenseDto expenseDto = ExpenseDto
                .builder()
                .expenseId(expenseId)
                .title(newTitle)
                .description(newDescription)
                .amount(newAmount)
                .categories(new HashSet<>())
                .build();

        ExpenseEntity updatedExpenseEntity = ExpenseEntity
                .builder()
                .expenseId(expenseId)
                .title(newTitle)
                .description(newDescription)
                .amount(newAmount)
                .categories(new HashSet<>())
                .build();

        when(this.expenseRepository.findByExpenseId(expenseId))
                .thenReturn(Optional.of(expenseEntity));
        when(this.expenseRepository.save(expenseEntity))
                .thenReturn(updatedExpenseEntity);
        when(this.expensesGroupRepository.findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));
        when(this.categoryRepository.findAllByCategoryIdIn(expenseDto.getCategoriesId()))
                .thenReturn(Optional.of(new ArrayList<>()));

        ExpenseDto updatedExpenseDto = this.expenseService.updateExpense(expenseId, expenseDto);

        assertEquals(expenseDto.getExpenseId(), updatedExpenseDto.getExpenseId());
        assertEquals(expenseDto.getTitle(), updatedExpenseDto.getTitle());
        assertEquals(expenseDto.getDescription(), updatedExpenseDto.getDescription());
        assertEquals(expenseDto.getAmount(), updatedExpenseDto.getAmount());
        assertEquals(expenseDto.getCategories(), updatedExpenseDto.getCategories());
    }

    @Test
    final void testUpdateExpense_AccessDeniedException() {

        String expenseId = "expenseId";

        List<CategoryEntity> categories = this.buildCategories();

        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();
        expensesGroupEntity.setUserId("another" + userId);

        ExpenseEntity expenseEntity = this.buildExpenseEntity();
        expenseEntity.setExpensesGroup(expensesGroupEntity);
        expenseEntity.setCategories(new HashSet<>(categories));

        String newTitle = "New title";
        String newDescription = "New description";
        BigDecimal newAmount = new BigDecimal(20);

        ExpenseDto expenseDto = ExpenseDto
                .builder()
                .expenseId(expenseId)
                .title(newTitle)
                .description(newDescription)
                .amount(newAmount)
                .categories(new HashSet<>())
                .build();

        when(this.expenseRepository.findByExpenseId(expenseId))
                .thenReturn(Optional.of(expenseEntity));
        when(this.expensesGroupRepository.findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));

        assertThrows(AccessDeniedException.class, () -> {
            this.expenseService.updateExpense(expenseId, expenseDto);
        });

        verify(this.expenseRepository, never()).save(any(ExpenseEntity.class));

    }

    @Test
    final void testDeleteExpense() {

        ExpenseEntity expenseEntity = this.buildExpenseEntity();
        expenseEntity.setExpensesGroup(this.buildExpensesGroupEntity());

        when(this.expenseRepository.findByExpenseId(expenseEntity.getExpenseId()))
                .thenReturn(Optional.of(expenseEntity));
        when(this.expensesGroupRepository
                .findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId()))
                .thenReturn(Optional.of(expenseEntity.getExpensesGroup()));

        this.expenseService.deleteExpense(expenseEntity.getExpenseId());

        verify(this.expenseRepository, times(1)).delete(expenseEntity);
    }

    @Test
    final void testDeleteExpense_AccessDeniedException() {

        ExpenseEntity expenseEntity = this.buildExpenseEntity();
        ExpensesGroupEntity expensesGroupEntity = this.buildExpensesGroupEntity();
        expensesGroupEntity.setUserId("another" + userId);
        expenseEntity.setExpensesGroup(expensesGroupEntity);

        when(this.expenseRepository.findByExpenseId(expenseEntity.getExpenseId()))
                .thenReturn(Optional.of(expenseEntity));
        when(this.expensesGroupRepository
                .findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId()))
                .thenReturn(Optional.of(expenseEntity.getExpensesGroup()));


        assertThrows(AccessDeniedException.class, () -> {
            this.expenseService.deleteExpense(expenseEntity.getExpenseId());
        });

        verify(this.expenseRepository, never()).delete(expenseEntity);
    }

    @Test
    final void testDeleteExpense_EntityNotFoundException() {

        ExpenseEntity expenseEntity = this.buildExpenseEntity();

        when(this.expenseRepository.findByExpenseId(expenseEntity.getExpenseId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.expenseService.deleteExpense(expenseEntity.getExpenseId());
        });

        verify(this.expenseRepository, never()).delete(expenseEntity);
    }
}
