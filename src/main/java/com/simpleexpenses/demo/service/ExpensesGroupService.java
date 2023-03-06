package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;

import java.util.List;

public interface ExpensesGroupService {

    ExpensesGroupDto createExpensesGroup(ExpensesGroupDto expensesGroup);

    List<ExpensesGroupDto> getExpensesGroups();
}
