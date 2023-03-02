package com.simpleexpenses.demo.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExpensesGroupResponse {

    private String expensesGroupId;
    private String title;
    private String description;
    private Date createdAt;

}
