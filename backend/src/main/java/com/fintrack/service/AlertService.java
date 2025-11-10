package com.fintrack.service;

import com.fintrack.dto.AlertResponse;
import com.fintrack.entity.*;
import com.fintrack.repository.AlertRepository;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les alertes.
 */
@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private GoalRepository goalRepository;

    /**
     * Récupère toutes les alertes d'un utilisateur.
     */
    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts(User user) {
        return alertRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les alertes non lues d'un utilisateur.
     */
    @Transactional(readOnly = true)
    public List<AlertResponse> getUnreadAlerts(User user) {
        return alertRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Compte le nombre d'alertes non lues.
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(User user) {
        return alertRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Marque une alerte comme lue.
     */
    @Transactional
    public void markAsRead(Long alertId, User user) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Non autorisé");
        }
        
        alert.setIsRead(true);
        alertRepository.save(alert);
    }

    /**
     * Marque toutes les alertes comme lues.
     */
    @Transactional
    public void markAllAsRead(User user) {
        List<Alert> alerts = alertRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        alerts.forEach(alert -> alert.setIsRead(true));
        alertRepository.saveAll(alerts);
    }

    /**
     * Supprime une alerte.
     */
    @Transactional
    public void deleteAlert(Long alertId, User user) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        
        if (!alert.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Non autorisé");
        }
        
        alertRepository.delete(alert);
    }

    /**
     * Génère les alertes de budget pour un utilisateur.
     */
    @Transactional
    public void generateBudgetAlerts(User user) {
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        
        for (Budget budget : budgets) {
            if (budget.isOverBudget()) {
                createBudgetExceededAlert(budget);
            } else if (budget.shouldAlert()) {
                createBudgetWarningAlert(budget);
            }
        }
    }

    /**
     * Génère les alertes d'objectifs pour un utilisateur.
     */
    @Transactional
    public void generateGoalAlerts(User user) {
        List<Goal> goals = goalRepository.findByUserId(user.getId());
        LocalDate today = LocalDate.now();
        
        for (Goal goal : goals) {
            if (goal.getTargetDate() != null && !goal.isCompleted()) {
                long daysUntilDeadline = ChronoUnit.DAYS.between(today, goal.getTargetDate());
                
                if (daysUntilDeadline <= 7 && daysUntilDeadline > 0) {
                    createGoalDeadlineAlert(goal, daysUntilDeadline);
                } else if (daysUntilDeadline < 0) {
                    createGoalOverdueAlert(goal);
                }
            }
        }
    }

    /**
     * Génère toutes les alertes (budget + objectifs).
     */
    @Transactional
    public void generateAllAlerts(User user) {
        alertRepository.deleteByUser(user);
        generateBudgetAlerts(user);
        generateGoalAlerts(user);
    }

    /**
     * Crée une alerte de budget dépassé.
     */
    private void createBudgetExceededAlert(Budget budget) {
        Alert alert = new Alert();
        alert.setUser(budget.getUser());
        alert.setType(AlertType.BUDGET_EXCEEDED);
        alert.setSeverity(AlertSeverity.CRITICAL);
        alert.setTitle("Budget dépassé");
        alert.setMessage(String.format(
                "Le budget '%s' a été dépassé de %.2f€ (%.0f%% du budget)",
                budget.getCategory().getName(),
                budget.getSpentAmount().subtract(budget.getAmount()).doubleValue(),
                budget.getPercentageUsed().doubleValue()
        ));
        alert.setBudget(budget);
        alert.setIsRead(false);
        
        alertRepository.save(alert);
    }

    /**
     * Crée une alerte d'avertissement de budget.
     */
    private void createBudgetWarningAlert(Budget budget) {
        Alert alert = new Alert();
        alert.setUser(budget.getUser());
        alert.setType(AlertType.BUDGET_WARNING);
        alert.setSeverity(AlertSeverity.WARNING);
        alert.setTitle("Attention au budget");
        alert.setMessage(String.format(
                "Le budget '%s' est utilisé à %.0f%% (%.2f€ / %.2f€)",
                budget.getCategory().getName(),
                budget.getPercentageUsed().doubleValue(),
                budget.getSpentAmount().doubleValue(),
                budget.getAmount().doubleValue()
        ));
        alert.setBudget(budget);
        alert.setIsRead(false);
        
        alertRepository.save(alert);
    }

    /**
     * Crée une alerte d'échéance d'objectif proche.
     */
    private void createGoalDeadlineAlert(Goal goal, long daysRemaining) {
        Alert alert = new Alert();
        alert.setUser(goal.getUser());
        alert.setType(AlertType.GOAL_DEADLINE_APPROACHING);
        alert.setSeverity(AlertSeverity.WARNING);
        alert.setTitle("Échéance d'objectif proche");
        alert.setMessage(String.format(
                "L'objectif '%s' arrive à échéance dans %d jour(s). Progression actuelle: %.0f%%",
                goal.getName(),
                daysRemaining,
                goal.getPercentageAchieved().doubleValue()
        ));
        alert.setGoal(goal);
        alert.setIsRead(false);
        
        alertRepository.save(alert);
    }

    /**
     * Crée une alerte d'objectif en retard.
     */
    private void createGoalOverdueAlert(Goal goal) {
        Alert alert = new Alert();
        alert.setUser(goal.getUser());
        alert.setType(AlertType.GOAL_DEADLINE_APPROACHING);
        alert.setSeverity(AlertSeverity.CRITICAL);
        alert.setTitle("Objectif en retard");
        alert.setMessage(String.format(
                "L'objectif '%s' est en retard. Progression actuelle: %.0f%%",
                goal.getName(),
                goal.getPercentageAchieved().doubleValue()
        ));
        alert.setGoal(goal);
        alert.setIsRead(false);
        
        alertRepository.save(alert);
    }

    /**
     * Convertit une entité Alert en AlertResponse.
     */
    private AlertResponse mapToResponse(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setId(alert.getId());
        response.setType(alert.getType());
        response.setSeverity(alert.getSeverity());
        response.setTitle(alert.getTitle());
        response.setMessage(alert.getMessage());
        response.setIsRead(alert.getIsRead());
        response.setCreatedAt(alert.getCreatedAt());
        
        if (alert.getBudget() != null) {
            response.setBudgetId(alert.getBudget().getId());
            response.setBudgetCategoryName(alert.getBudget().getCategory().getName());
        }
        
        if (alert.getGoal() != null) {
            response.setGoalId(alert.getGoal().getId());
            response.setGoalName(alert.getGoal().getName());
        }
        
        return response;
    }
}
