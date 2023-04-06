package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.dto.PagedDto;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Test
    final void testDeleteExpensesGroup() {

        when(this.expensesGroupRepository.findByExpensesGroupId(expensesGroupEntity.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroupEntity));

        this.expensesGroupService.deleteExpensesGroup(expensesGroupEntity.getExpensesGroupId());

        verify(expensesGroupRepository, times(1)).delete(expensesGroupEntity);
    }

    @Test
    final void testDeleteExpensesGroup_EntityNotFoundException() {

        when(this.expensesGroupRepository.findByExpensesGroupId(expensesGroupEntity.getExpensesGroupId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.expensesGroupService.deleteExpensesGroup(expensesGroupEntity.getExpensesGroupId());
        });

        verify(expensesGroupRepository, times(0)).delete(expensesGroupEntity);
    }

    @Test
    final void testDeleteExpensesGroup_AccessDeniedException() {

        ExpensesGroupEntity expensesGroup = ExpensesGroupEntity
                .builder()
                .expensesGroupId("groupId")
                .title(title)
                .description(description)
                .userId("anotherUserId")
                .build();

        when(this.expensesGroupRepository.findByExpensesGroupId(expensesGroup.getExpensesGroupId()))
                .thenReturn(Optional.of(expensesGroup));

        assertThrows(AccessDeniedException.class, () -> {
            this.expensesGroupService.deleteExpensesGroup(expensesGroup.getExpensesGroupId());
        });

        verify(expensesGroupRepository, times(0)).delete(expensesGroup);
    }

    @Test
    final void testGetExpenses() {

        ExpenseEntity exp1 = ExpenseEntity.builder()
                .expenseId("id1")
                .title("t1")
                .description("d1")
                .amount(new BigDecimal(10))
                .build();
        ExpenseEntity exp2 = ExpenseEntity.builder()
                .expenseId("id2")
                .title("t2")
                .description("d2")
                .amount(new BigDecimal(10))
                .build();
        ExpenseEntity exp3 = ExpenseEntity.builder()
                .expenseId("id3")
                .title("t3")
                .description("d3")
                .amount(new BigDecimal(10))
                .build();

        List<ExpenseEntity> expenseEntities = new ArrayList<>();
        expenseEntities.add(exp1);
        expenseEntities.add(exp2);
        expenseEntities.add(exp3);

        String groupId = "groupId";
        int page = 0;
        int size = 10;

        when(this.expensesGroupRepository.findByExpensesGroupId(groupId))
                .thenReturn(Optional.of(this.expensesGroupEntity));
        when(this.expenseRepository.findAllByExpensesGroup_Id(this.expensesGroupEntity.getId(),  PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"))))
                .thenReturn(Optional.of(new PageImpl<>(expenseEntities)));

        PagedDto<ExpenseDto> pagedExpensesDto = this.expensesGroupService.getExpenses(groupId, page, size);

        assertEquals(page, pagedExpensesDto.getPage());
        assertEquals(1, pagedExpensesDto.getTotalPages());
        assertEquals(3, pagedExpensesDto.getTotalElements());
    }

    @Test
    final void testGetExpenses_EntityNotFoundException_ExpensesGroup() {

        String groupId = "groupId";
        int page = 0;
        int size = 10;

        when(this.expensesGroupRepository.findByExpensesGroupId(groupId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            PagedDto<ExpenseDto> pagedExpensesDto = this.expensesGroupService.getExpenses(groupId, page, size);
        });
    }

    @Test
    final void testGetExpenses_EntityNotFoundException_Expenses() {

        String groupId = "groupId";
        int page = 0;
        int size = 10;

        when(this.expensesGroupRepository.findByExpensesGroupId(groupId))
                .thenReturn(Optional.of(this.expensesGroupEntity));
        when(this.expenseRepository.findAllByExpensesGroup_Id(this.expensesGroupEntity.getId(),  PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"))))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            PagedDto<ExpenseDto> pagedExpensesDto = this.expensesGroupService.getExpenses(groupId, page, size);
        });
    }

    @Test
    final void testGetExpenses_AccessDeniedException() {

        String groupId = "groupId";
        int page = 0;
        int size = 10;

        this.expensesGroupEntity.setUserId("anotherUserId");
        when(this.expensesGroupRepository.findByExpensesGroupId(groupId))
                .thenReturn(Optional.of(this.expensesGroupEntity));

        assertThrows(AccessDeniedException.class, () -> {
            PagedDto<ExpenseDto> pagedExpensesDto = this.expensesGroupService.getExpenses(groupId, page, size);
        });
    }
}
