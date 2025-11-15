package com.fintrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une position dans le portefeuille d'actions d'un utilisateur.
 * Cette entité agrège les transactions pour calculer la position actuelle.
 */
@Entity
@Table(name = "portfolio_positions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "symbol"}),
       indexes = {
           @Index(name = "idx_portfolio_user", columnList = "user_id"),
           @Index(name = "idx_portfolio_symbol", columnList = "symbol")
       })
@EntityListeners(AuditingEntityListener.class)
public class PortfolioPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Le symbole est obligatoire")
    @Column(nullable = false, length = 10)
    private String symbol;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @NotNull(message = "La quantité totale est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @NotNull(message = "Le prix moyen d'achat est obligatoire")
    @DecimalMin(value = "0.00", message = "Le prix moyen ne peut pas être négatif")
    @Column(name = "average_buy_price", nullable = false, precision = 12, scale = 4)
    private BigDecimal averageBuyPrice;

    @NotNull(message = "Le montant total investi est obligatoire")
    @DecimalMin(value = "0.00", message = "Le montant investi ne peut pas être négatif")
    @Column(name = "total_invested", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalInvested;

    @Column(name = "current_price", precision = 12, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "current_value", precision = 12, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "gain_loss", precision = 12, scale = 2)
    private BigDecimal gainLoss;

    @Column(name = "gain_loss_percent", precision = 8, scale = 4)
    private BigDecimal gainLossPercent;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructeurs
    public PortfolioPosition() {
    }

    public PortfolioPosition(User user, String symbol, String companyName, Integer totalQuantity,
                            BigDecimal averageBuyPrice, BigDecimal totalInvested) {
        this.user = user;
        this.symbol = symbol;
        this.companyName = companyName;
        this.totalQuantity = totalQuantity;
        this.averageBuyPrice = averageBuyPrice;
        this.totalInvested = totalInvested;
    }

    // Méthodes utilitaires
    public void updateCurrentMetrics(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        if (this.totalQuantity != null && currentPrice != null) {
            this.currentValue = currentPrice.multiply(new BigDecimal(this.totalQuantity));
            if (this.totalInvested != null && this.totalInvested.compareTo(BigDecimal.ZERO) > 0) {
                this.gainLoss = this.currentValue.subtract(this.totalInvested);
                this.gainLossPercent = this.gainLoss
                    .divide(this.totalInvested, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
        }
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getAverageBuyPrice() {
        return averageBuyPrice;
    }

    public void setAverageBuyPrice(BigDecimal averageBuyPrice) {
        this.averageBuyPrice = averageBuyPrice;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getGainLoss() {
        return gainLoss;
    }

    public void setGainLoss(BigDecimal gainLoss) {
        this.gainLoss = gainLoss;
    }

    public BigDecimal getGainLossPercent() {
        return gainLossPercent;
    }

    public void setGainLossPercent(BigDecimal gainLossPercent) {
        this.gainLossPercent = gainLossPercent;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
