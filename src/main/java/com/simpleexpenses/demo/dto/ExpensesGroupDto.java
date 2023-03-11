package com.simpleexpenses.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpensesGroupDto {

    private Long id;
    private String expensesGroupId;
    private String userId;
    private String title;
    private String description;
    private Date createdAt;
    private List<ExpenseDto> expenses = new ArrayList<>();

}
