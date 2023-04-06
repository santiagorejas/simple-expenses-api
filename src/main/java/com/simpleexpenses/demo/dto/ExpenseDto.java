package com.simpleexpenses.demo.dto;

import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private long id;
    private String expenseId;
    private String expensesGroupId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date date;
    private ExpensesGroupEntity expensesGroup;
    private Set<CategoryDto> categories = new HashSet<>();
    private List<String> categoriesId = new ArrayList<>();
}
