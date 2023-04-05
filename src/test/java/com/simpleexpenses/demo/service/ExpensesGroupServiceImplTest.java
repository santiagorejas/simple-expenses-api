package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpensesGroupServiceImplTest {

    @InjectMocks
    ExpensesGroupServiceImpl expensesGroupService;

    @Mock
    ExpensesGroupRepository expensesGroupRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @Test
    final void testCreateExpenseGroup() {

        String title = "Expenses group test.";
        String description = "Description.";

        ExpensesGroupDto expensesGroupDto = ExpensesGroupDto
                .builder()
                .title(title)
                .description(description)
                .build();

        ExpensesGroupEntity expensesGroupEntity = ExpensesGroupEntity
                .builder()
                .title(title)
                .description(description)
                .build();

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("userid");
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        when(this.expensesGroupRepository.save(any(ExpensesGroupEntity.class))).thenReturn(expensesGroupEntity);

        ExpensesGroupDto createdExpensesGroup =
                this.expensesGroupService.createExpensesGroup(expensesGroupDto);

        assertEquals(expensesGroupDto.getTitle(), createdExpensesGroup.getTitle());
        assertEquals(expensesGroupDto.getDescription(), createdExpensesGroup.getDescription());
    }

}
