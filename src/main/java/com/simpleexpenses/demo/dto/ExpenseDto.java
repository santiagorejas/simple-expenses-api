package com.simpleexpenses.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpenseDto {

    private long id;
    private String expenseId;
    private String expensesGroupId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date date;
    private List<CategoryDto> categories = new ArrayList<>();
    private List<String> categoriesId = new ArrayList<>();
}
