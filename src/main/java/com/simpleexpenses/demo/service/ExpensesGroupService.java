package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.dto.PagedDto;

import java.util.List;

public interface ExpensesGroupService {

    ExpensesGroupDto createExpensesGroup(ExpensesGroupDto expensesGroup);

    List<ExpensesGroupDto> getExpensesGroups();

    ExpensesGroupDto updateExpensesGroup(String groupId, ExpensesGroupDto expensesGroupDto);

    void deleteExpensesGroup(String groupId);

    PagedDto<ExpenseDto> getExpenses(String groupId, int page, int size);

    ExpensesGroupDto getExpensesGroup(String groupId);
}
