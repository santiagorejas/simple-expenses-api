package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.impl.ExpensesGroupServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpensesGroupServiceImplTest {

    @InjectMocks
    ExpensesGroupServiceImpl expensesGroupService;

    @Mock
    ExpensesGroupRepository expensesGroupRepository;

    @Mock
    ExpenseRepository expenseRepository;

    private ExpensesGroupEntity expensesGroupEntity;

    private final String title = "Expenses group test.";
    private final String description = "Description.";
    private final String userId = "userId";

    @BeforeEach
    void setUp() {
        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        this.expensesGroupEntity = ExpensesGroupEntity
                .builder()
                .userId(userId)
                .title(title)
                .description(description)
                .build();
    }

    @Test
    final void testCreateExpenseGroup() {

        ExpensesGroupDto expensesGroupDto = ExpensesGroupDto
                .builder()
                .title(title)
                .description(description)
                .build();

        when(this.expensesGroupRepository.save(any(ExpensesGroupEntity.class))).thenReturn(this.expensesGroupEntity);

        ExpensesGroupDto createdExpensesGroup =
                this.expensesGroupService.createExpensesGroup(expensesGroupDto);

        assertEquals(expensesGroupDto.getTitle(), createdExpensesGroup.getTitle());
        assertEquals(expensesGroupDto.getDescription(), createdExpensesGroup.getDescription());
    }

    @Test
    final void testGetExpensesGroup() {

        String userId = "userid123";

        ExpensesGroupEntity exp1 = ExpensesGroupEntity.builder().title("Exp1 title").userId(userId).description("Description 1").build();
        ExpensesGroupEntity exp2 = ExpensesGroupEntity.builder().title("Exp2 title").userId(userId).description("Description 2").build();
        ExpensesGroupEntity exp3 = ExpensesGroupEntity.builder().title("Exp3 title").userId(userId).description("Description 3").build();

        List<ExpenseEntity> expenses = new ArrayList<>();

        ExpenseEntity expenseEntity = ExpenseEntity
                .builder()
                .title("Expense title")
                .description("Expense description")
                .amount(new BigDecimal(20.0))
                .date(new Date())
                .build();
        expenses.add(expenseEntity);

        exp2.setExpenses(expenses);

        List<ExpensesGroupEntity> expensesGroupEntities = new ArrayList<>();
        expensesGroupEntities.add(exp1);
        expensesGroupEntities.add(exp2);
        expensesGroupEntities.add(exp3);

        when(this.expensesGroupRepository.findAllByUserId(userId)).thenReturn(Optional.of(expensesGroupEntities));

        List<ExpensesGroupDto> expensesGroupDtoList = this.expensesGroupService.getExpensesGroups();

        assertEquals(expensesGroupDtoList.size(), expensesGroupEntities.size());

        ExpensesGroupDto currentDto;
        ExpensesGroupEntity currentEntity;

        for (int i = 0; i < expensesGroupDtoList.size(); i++) {

            currentDto = expensesGroupDtoList.get(i);
            currentEntity = expensesGroupEntities.get(i);

            assertEquals(currentDto.getTitle(), currentEntity.getTitle());
            assertEquals(currentDto.getDescription(), currentEntity.getDescription());;

            if (currentDto.getExpenses() != null) {
                assertEquals(currentDto.getExpenses().size(), currentEntity.getExpenses().size());
            }
        }
    }

    @Test
    final void testUpdateExpensesGroup() {

        String newTitle = "This is the new title.";
        String newDescription = "This is the new Description.";
        String expensesGroupId = UUID.randomUUID().toString();

        ExpensesGroupDto expensesGroupDto = ExpensesGroupDto
                .builder()
                .expensesGroupId(expensesGroupId)
                .title(newTitle)
                .description(newDescription)
                .userId(userId)
                .build();

        ExpensesGroupEntity updatedExpensesGroupEntity = ExpensesGroupEntity
                .builder()
                .expensesGroupId(expensesGroupId)
                .title(newTitle)
                .description(newDescription)
                .userId(userId)
                .build();

        when(this.expensesGroupRepository.findByExpensesGroupId(anyString())).thenReturn(Optional.of(this.expensesGroupEntity));
        when(this.expensesGroupRepository.save(any(ExpensesGroupEntity.class))).thenReturn(updatedExpensesGroupEntity);

        ExpensesGroupDto updatedExpensesGroupDto = this.expensesGroupService.updateExpensesGroup("someId", expensesGroupDto);

        assertEquals(updatedExpensesGroupDto.getTitle(), expensesGroupDto.getTitle());
        assertEquals(updatedExpensesGroupDto.getDescription(), expensesGroupDto.getDescription());
        assertEquals(updatedExpensesGroupDto.getUserId(), expensesGroupDto.getUserId());
        assertEquals(updatedExpensesGroupDto.getExpensesGroupId(), expensesGroupDto.getExpensesGroupId());

    }

    @Test
    final void testUpdateExpensesGroup_EntityNotFoundException() {

        String newTitle = "This is the new title.";
        String newDescription = "This is the new Description.";

        ExpensesGroupDto expensesGroupDto = ExpensesGroupDto
                .builder()
                .title(newTitle)
                .description(newDescription)
                .build();

        when(this.expensesGroupRepository.findByExpensesGroupId(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.expensesGroupService.updateExpensesGroup("someId", expensesGroupDto);
        });
    }

    @Test
    final void testUpdateExpensesGroup_AccessDeniedException() {

        String newTitle = "This is the new title.";
        String newDescription = "This is the new Description.";

        ExpensesGroupDto expensesGroupDto = ExpensesGroupDto
                .builder()
                .title(newTitle)
                .description(newDescription)
                .build();

        ExpensesGroupEntity expensesGroup = ExpensesGroupEntity
                .builder()
                .title(title)
                .description(description)
                .userId("anotherUserId")
                .build();

        when(this.expensesGroupRepository.findByExpensesGroupId(anyString())).thenReturn(Optional.of(expensesGroup));

        assertThrows(AccessDeniedException.class, () -> {
            this.expensesGroupService.updateExpensesGroup("someId", expensesGroupDto);
        });
    }

}
