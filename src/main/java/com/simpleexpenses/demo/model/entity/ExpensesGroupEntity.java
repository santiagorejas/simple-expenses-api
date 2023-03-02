package com.simpleexpenses.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

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

}
