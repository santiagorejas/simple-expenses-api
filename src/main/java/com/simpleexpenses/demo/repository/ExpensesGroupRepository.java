package com.simpleexpenses.demo.repository;

import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesGroupRepository extends JpaRepository<ExpensesGroupEntity, Long> {

}
