package com.simpleexpenses.demo.repository;

import com.simpleexpenses.demo.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<List<CategoryEntity>> findAllByUserId(String userId);

    Optional<CategoryEntity> findByCategoryId(String categoryId);
}
