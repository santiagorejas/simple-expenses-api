package com.simpleexpenses.demo.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesGroupDto {

    private Long id;
    private String expensesGroupId;
    private String userId;
    private String title;
    private String description;
    private Date createdAt;
    private List<ExpenseDto> expenses = new ArrayList<>();

}
