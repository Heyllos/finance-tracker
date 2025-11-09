package com.fintrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un objectif d'épargne.
 * Permet de définir un montant cible à atteindre pour un projet.
 */
@Entity
@Table(name = "goals", indexes = {
    @Index(name = "idx_goal_user", columnList = "user_id"),
    @Index(name = "idx_goal_status", columnList = "status"),
    @Index(name = "idx_goal_deadline", columnList = "target_date")
})
@EntityListeners(AuditingEntityListener.class)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'objectif est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;

    @NotNull(message = "Le montant cible est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant cible doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 10 chiffres entiers et 2 décimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @DecimalMin(value = "0.00", message = "Le montant actuel ne peut pas être négatif")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "target_date")
    private LocalDate targetDate; // Date cible (optionnelle)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalPriority priority = GoalPriority.MEDIUM;

    // Relation avec User (un objectif appartient à un utilisateur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 50)
    @Column(length = 50)
    private String icon; // Icône représentant l'objectif (ex: "home", "car", "plane")

    @Size(max = 7)
    @Column(length = 7)
    private String color; // Couleur associée (code hexadécimal)

    @Column(nullable = false)
    private Boolean isArchived = false;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    @Column(precision = 5, scale = 2)
    private BigDecimal allocationPercentage; // Pourcentage d'épargne mensuelle allouée à cet objectif

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt; // Date de complétion

    // Constructeurs
    public Goal() {
    }

    public Goal(String name, BigDecimal targetAmount, LocalDate targetDate, User user) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.user = user;
        this.currentAmount = BigDecimal.ZERO;
        this.status = GoalStatus.IN_PROGRESS;
        this.priority = GoalPriority.MEDIUM;
        this.isArchived = false;
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

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public BigDecimal getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(BigDecimal allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // Méthodes utilitaires
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }

    public BigDecimal getPercentageAchieved() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }

    public void addContribution(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (isCompleted() && this.status != GoalStatus.COMPLETED) {
            this.status = GoalStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
        }
    }

    public boolean isOverdue() {
        return targetDate != null && 
               LocalDate.now().isAfter(targetDate) && 
               status == GoalStatus.IN_PROGRESS;
    }
}
