package com.simpleexpenses.demo.service;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.model.request.ExpensesGroupModelRequest;

import java.util.List;

public interface ExpensesGroupService {

    ExpensesGroupDto createExpensesGroup(ExpensesGroupDto expensesGroup);

    List<ExpensesGroupDto> getExpensesGroups();

    void updateExpensesGroup(String groupId, ExpensesGroupDto expensesGroupDto);

    void deleteExpensesGroup(String groupId);
}
