package com.simpleexpenses.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "expenses_groups")
public class ExpensesGroupEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length  = 36, nullable = false)
    private String expensesGroupId;

    @Column(length = 21, nullable = false)
    private String userId;

    @Column(length = 120, nullable = false)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "expensesGroup" , cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<ExpenseEntity> expenses = new ArrayList<>();

}
