package com.simpleexpenses.demo.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ExpenseRequest {

    private String expensesGroupId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date date;

}
