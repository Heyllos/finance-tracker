package com.fintrack.dto;

import com.fintrack.entity.GoalPriority;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour créer ou modifier un objectif.
 */
public class GoalRequest {

    @NotBlank(message = "Le nom de l'objectif est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "Le montant cible est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant cible doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal targetAmount;

    private LocalDate targetDate;

    private GoalPriority priority = GoalPriority.MEDIUM;

    @Size(max = 50)
    private String icon;

    @Size(max = 7)
    private String color;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal allocationPercentage;

    // Constructeurs
    public GoalRequest() {
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(BigDecimal allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }
}
