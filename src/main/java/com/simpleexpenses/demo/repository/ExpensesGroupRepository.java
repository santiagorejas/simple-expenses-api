package com.simpleexpenses.demo.repository;

import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesGroupRepository extends JpaRepository<ExpensesGroupEntity, Long> {

    Optional<List<ExpensesGroupEntity>> findAllByUserId(String userId);

    Optional<ExpensesGroupEntity> findByExpensesGroupId(String expensesGroupId);

}
