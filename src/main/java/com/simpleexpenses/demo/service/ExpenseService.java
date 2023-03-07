package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpenseDto;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseDto expenseDto);

    ExpenseDto updateExpense(String expenseId, ExpenseDto expenseDto);

    void deleteExpense(String expenseId);

    void addCategory(String categoryId, String expenseId);
}
