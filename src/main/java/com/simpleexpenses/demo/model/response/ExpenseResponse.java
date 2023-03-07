package com.simpleexpenses.demo.model.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ExpenseResponse {

    private String expenseId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date date;
    private List<CategoryResponse> categories = new ArrayList<>();

}
