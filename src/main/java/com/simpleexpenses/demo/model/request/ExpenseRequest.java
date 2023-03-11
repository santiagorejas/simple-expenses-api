package com.simpleexpenses.demo.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpenseRequest {

    private String expensesGroupId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date date;
    private List<String> categoriesId = new ArrayList<>();

}
