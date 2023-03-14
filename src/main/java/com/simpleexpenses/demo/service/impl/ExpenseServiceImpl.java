package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.CategoryEntity;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.CategoryRepository;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.CategoryService;
import com.simpleexpenses.demo.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpensesGroupRepository expensesGroupRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    private void setExpensesCategories(ExpenseEntity expenseEntity, List<String> categoryId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<CategoryEntity> categories = this.categoryRepository
                .findAllByCategoryIdIn(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category doesn't exist."));

        for (CategoryEntity category: categories) {
            if (!category.getUserId().equals(userId)) {
                throw new AccessDeniedException("You don't own this category.");
            }
            expenseEntity.getCategories().add(category);
        }
    }

    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto) {

        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupId(expenseDto.getExpensesGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist. Id provided: " + expenseDto.getExpensesGroupId()));

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

        setExpensesCategories(expenseEntity, expenseDto.getCategoriesId());

        ExpenseEntity storedExpenseEntity = this.expenseRepository.save(expenseEntity);

        ExpenseDto storedExpenseDto = modelMapper.map(storedExpenseEntity, ExpenseDto.class);

        return storedExpenseDto;
    }

    private void checkExpenseAccess(ExpenseEntity expenseEntity) {
        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupId(expenseEntity.getExpensesGroup().getExpensesGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expensesGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't own this expenses group.");
        }
    }

    @Override
    public ExpenseDto updateExpense(String expenseId, ExpenseDto expenseDto) {

        ExpenseEntity expenseEntity = this.expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(()  -> new EntityNotFoundException("Expense doesn't exist."));

        checkExpenseAccess(expenseEntity);

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        expenseEntity.setTitle(expenseDto.getTitle());
        expenseEntity.setDescription(expenseDto.getDescription());
        expenseEntity.setAmount(expenseDto.getAmount());
        expenseEntity.setDate(expenseDto.getDate());

        expenseEntity.getCategories().clear();
        setExpensesCategories(expenseEntity, expenseDto.getCategoriesId());

        ExpenseEntity storedExpenseEntity = this.expenseRepository.save(expenseEntity);

        ExpenseDto updatedExpenseDto = modelMapper.map(storedExpenseEntity, ExpenseDto.class);

        return updatedExpenseDto;

    }

    @Override
    public void deleteExpense(String expenseId) {

        ExpenseEntity expenseEntity = this.expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(()  -> new EntityNotFoundException("Expense doesn't exist."));

        checkExpenseAccess(expenseEntity);

        this.expenseRepository.delete(expenseEntity);

    }

    @Override
    public void addCategory(String categoryId, String expenseId) {

        ExpenseEntity expenseEntity = this.expenseRepository
                .findByExpenseId(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense doesn't exist."));

        checkExpenseAccess(expenseEntity);

        CategoryEntity categoryEntity = this.categoryRepository
                .findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!categoryEntity.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can't use this category.");
        }

        if (!expenseEntity.getCategories().contains(categoryEntity)) {
            expenseEntity.getCategories().add(categoryEntity);
            this.expenseRepository.save(expenseEntity);
        }

    }

    @Override
    public void removeCategory(String categoryId, String expenseId) {

        ExpenseEntity expenseEntity = this.expenseRepository
                .findByExpenseId(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense doesn't exist."));

        checkExpenseAccess(expenseEntity);

        CategoryEntity categoryEntity = this.categoryRepository
                .findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category doesn't exist."));

        if (expenseEntity.getCategories().contains(categoryEntity)) {
            expenseEntity.getCategories().remove(categoryEntity);
            this.expenseRepository.save(expenseEntity);
        }

    }
}
