package com.fintrack.dto;

import com.fintrack.entity.Goal;
import com.fintrack.entity.GoalPriority;
import com.fintrack.entity.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoalResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageAchieved;
    private LocalDate targetDate;
    private GoalStatus status;
    private GoalPriority priority;
    private String icon;
    private String color;
    private Boolean isArchived;
    private Boolean isCompleted;
    private Boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public GoalResponse() {}

    public GoalResponse(Goal goal) {
        this.id = goal.getId();
        this.name = goal.getName();
        this.description = goal.getDescription();
        this.targetAmount = goal.getTargetAmount();
        this.currentAmount = goal.getCurrentAmount();
        this.remainingAmount = goal.getRemainingAmount();
        this.percentageAchieved = goal.getPercentageAchieved();
        this.targetDate = goal.getTargetDate();
        this.status = goal.getStatus();
        this.priority = goal.getPriority();
        this.icon = goal.getIcon();
        this.color = goal.getColor();
        this.isArchived = goal.getIsArchived();
        this.isCompleted = goal.isCompleted();
        this.isOverdue = goal.isOverdue();
        this.createdAt = goal.getCreatedAt();
        this.updatedAt = goal.getUpdatedAt();
        this.completedAt = goal.getCompletedAt();
    }

    // Getters et Setters générés automatiquement
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public BigDecimal getPercentageAchieved() { return percentageAchieved; }
    public void setPercentageAchieved(BigDecimal percentageAchieved) { this.percentageAchieved = percentageAchieved; }
    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }
    public GoalPriority getPriority() { return priority; }
    public void setPriority(GoalPriority priority) { this.priority = priority; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
