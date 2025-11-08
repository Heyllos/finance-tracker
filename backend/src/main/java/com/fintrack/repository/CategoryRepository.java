package com.fintrack.repository;

import com.fintrack.entity.Category;
import com.fintrack.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Category.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, TransactionType type);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    List<Category> findByUserIdAndIsDefault(Long userId, Boolean isDefault);

    boolean existsByNameAndUserId(String name, Long userId);
}
