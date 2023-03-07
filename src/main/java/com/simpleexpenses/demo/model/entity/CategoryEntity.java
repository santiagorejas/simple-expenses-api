package com.simpleexpenses.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name ="categories")
public class CategoryEntity {

    @Id
    @GeneratedValue
    private long Id;

    @Column(nullable = false, length = 36)
    private String categoryId;

    @Column(nullable = false, length = 21)
    private String userId;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 6)
    private String color;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "expenses_categories",
            joinColumns = {@JoinColumn(name = "category_id")},
            inverseJoinColumns = {@JoinColumn(name = "expense_id")})
    private List<ExpenseEntity> expenses;
}
