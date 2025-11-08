package com.fintrack.dto;

import com.fintrack.entity.Budget;
import com.fintrack.entity.RecurrenceFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un budget.
 */
public class BudgetResponse {

    private Long id;
    private String name;
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageUsed;
    private LocalDate startDate;
    private LocalDate endDate;
    private CategoryResponse category;
    private Boolean isRecurring;
    private RecurrenceFrequency recurrenceFrequency;
    private Boolean alertEnabled;
    private BigDecimal alertThreshold;
    private Boolean isActive;
    private Boolean isOverBudget;
    private Boolean shouldAlert;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public BudgetResponse() {
    }

    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.name = budget.getName();
        this.amount = budget.getAmount();
        this.spentAmount = budget.getSpentAmount();
        this.remainingAmount = budget.getRemainingAmount();
        this.percentageUsed = budget.getPercentageUsed();
        this.startDate = budget.getStartDate();
        this.endDate = budget.getEndDate();
        this.category = new CategoryResponse(budget.getCategory());
        this.isRecurring = budget.getIsRecurring();
        this.recurrenceFrequency = budget.getRecurrenceFrequency();
        this.alertEnabled = budget.getAlertEnabled();
        this.alertThreshold = budget.getAlertThreshold();
        this.isActive = budget.getIsActive();
        this.isOverBudget = budget.isOverBudget();
        this.shouldAlert = budget.shouldAlert();
        this.createdAt = budget.getCreatedAt();
        this.updatedAt = budget.getUpdatedAt();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(BigDecimal percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public RecurrenceFrequency getRecurrenceFrequency() {
        return recurrenceFrequency;
    }

    public void setRecurrenceFrequency(RecurrenceFrequency recurrenceFrequency) {
        this.recurrenceFrequency = recurrenceFrequency;
    }

    public Boolean getAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(Boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public BigDecimal getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(BigDecimal alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsOverBudget() {
        return isOverBudget;
    }

    public void setIsOverBudget(Boolean isOverBudget) {
        this.isOverBudget = isOverBudget;
    }

    public Boolean getShouldAlert() {
        return shouldAlert;
    }

    public void setShouldAlert(Boolean shouldAlert) {
        this.shouldAlert = shouldAlert;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
