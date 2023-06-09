package com.simpleexpenses.demo.repository;

import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    Optional<ExpenseEntity> findByExpenseId(String expenseId);

    Optional<Page<ExpenseEntity>> findAllByExpensesGroup_Id(Long groupId, Pageable pageable);
}
