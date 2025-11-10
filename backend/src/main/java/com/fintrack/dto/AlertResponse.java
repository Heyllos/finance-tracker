package com.fintrack.dto;

import com.fintrack.entity.AlertSeverity;
import com.fintrack.entity.AlertType;

import java.time.LocalDateTime;

/**
 * DTO pour envoyer les données d'une alerte au frontend.
 */
public class AlertResponse {
    
    private Long id;
    private AlertType type;
    private AlertSeverity severity;
    private String title;
    private String message;
    private Long budgetId;
    private String budgetCategoryName;
    private Long goalId;
    private String goalName;
    private Boolean isRead;
    private LocalDateTime createdAt;

    // Constructeurs
    public AlertResponse() {
    }

    public AlertResponse(Long id, AlertType type, AlertSeverity severity, String title, 
                        String message, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetCategoryName() {
        return budgetCategoryName;
    }

    public void setBudgetCategoryName(String budgetCategoryName) {
        this.budgetCategoryName = budgetCategoryName;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
