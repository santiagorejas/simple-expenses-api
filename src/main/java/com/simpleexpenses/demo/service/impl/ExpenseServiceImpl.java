package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpensesGroupRepository expensesGroupRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto) {

        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupId(expenseDto.getExpensesGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expensesGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't own this expenses group.");
        }

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        ExpenseEntity expenseEntity = modelMapper.map(expenseDto, ExpenseEntity.class);
        expenseEntity.setExpenseId(UUID.randomUUID().toString());
        expenseEntity.setExpensesGroup(expensesGroup);

        ExpenseEntity storedExpenseEntity = this.expenseRepository.save(expenseEntity);

        ExpenseDto storedExpenseDto = modelMapper.map(storedExpenseEntity, ExpenseDto.class);

        return storedExpenseDto;
    }

    @Override
    public ExpenseDto updateExpense(String expenseId, ExpenseDto expenseDto) {

        ExpenseEntity expenseEntity = this.expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(()  -> new EntityNotFoundException("Expense doesn't exist."));

        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expensesGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't own this expenses group.");
        }

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        expenseEntity.setTitle(expenseDto.getTitle());
        expenseEntity.setDescription(expenseDto.getDescription());
        expenseEntity.setAmount(expenseDto.getAmount());
        expenseEntity.setDate(expenseDto.getDate());

        ExpenseEntity storedExpenseEntity = this.expenseRepository.save(expenseEntity);

        ExpenseDto updatedExpenseDto = modelMapper.map(storedExpenseEntity, ExpenseDto.class);

        return updatedExpenseDto;

    }
}
