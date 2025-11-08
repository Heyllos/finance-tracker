package com.fintrack.repository;

import com.fintrack.entity.Goal;
import com.fintrack.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Goal.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);

    List<Goal> findByUserIdAndIsArchived(Long userId, Boolean isArchived);

    Optional<Goal> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.status = :status AND g.isArchived = false " +
           "ORDER BY g.priority DESC, g.targetDate ASC")
    List<Goal> findActiveGoalsByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") GoalStatus status
    );

    @Query("SELECT g FROM Goal g WHERE g.targetDate < :date " +
           "AND g.status = 'IN_PROGRESS' AND g.isArchived = false")
    List<Goal> findOverdueGoals(@Param("date") LocalDate date);

    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId " +
           "AND g.currentAmount >= g.targetAmount " +
           "AND g.status != 'COMPLETED'")
    List<Goal> findCompletedButNotMarkedGoals(@Param("userId") Long userId);
}
