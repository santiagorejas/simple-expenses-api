package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpenseDto;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseDto expenseDto);
}
