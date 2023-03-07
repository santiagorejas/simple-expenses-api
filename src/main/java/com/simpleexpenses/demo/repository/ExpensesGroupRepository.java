package com.simpleexpenses.demo.repository;

import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesGroupRepository extends JpaRepository<ExpensesGroupEntity, Long> {

    Optional<List<ExpensesGroupEntity>> findAllByUserId(String userId);

    Optional<ExpensesGroupEntity> findByExpensesGroupId(String expensesGroupId);

    @Query("SELECT g FROM expenses_groups g LEFT JOIN FETCH g.expenses WHERE g.expensesGroupId = :groupId")
    Optional<ExpensesGroupEntity> findByExpensesGroupIdPopulated(@Param("groupId") String groupId);
}
