package com.simpleexpenses.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name =" expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length  = 36, nullable = false)
    private String expenseId;

    @Column(length = 120, nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    private ExpensesGroupEntity expensesGroup;

}