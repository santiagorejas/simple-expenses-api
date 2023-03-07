package com.simpleexpenses.demo.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpensesGroupResponse {

    private String expensesGroupId;
    private String title;
    private String description;
    private Date createdAt;
    private List<ExpenseResponse> expenses;

}
