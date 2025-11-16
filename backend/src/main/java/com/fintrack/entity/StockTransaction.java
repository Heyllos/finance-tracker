package com.fintrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction d'achat ou de vente d'actions.
 */
@Entity
@Table(name = "stock_transactions", indexes = {
    @Index(name = "idx_stock_transaction_user", columnList = "user_id"),
    @Index(name = "idx_stock_transaction_symbol", columnList = "symbol"),
    @Index(name = "idx_stock_transaction_date", columnList = "transaction_date")
})
@EntityListeners(AuditingEntityListener.class)
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Le symbole est obligatoire")
    @Size(min = 1, max = 10, message = "Le symbole doit contenir entre 1 et 10 caractères")
    @Column(nullable = false, length = 10)
    private String symbol;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @NotNull(message = "Le type de transaction est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionType type;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au minimum de 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Le prix par action est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @Digits(integer = 10, fraction = 4, message = "Le prix doit avoir au maximum 10 chiffres entiers et 4 décimales")
    @Column(name = "price_per_share", nullable = false, precision = 12, scale = 4)
    private BigDecimal pricePerShare;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant total doit être supérieur à 0")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.00", message = "Les frais ne peuvent pas être négatifs")
    @Column(precision = 12, scale = 2)
    private BigDecimal fees;

    @NotNull(message = "La date de transaction est obligatoire")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Size(max = 500, message = "Les notes ne doivent pas dépasser 500 caractères")
    @Column(length = 500)
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructeurs
    public StockTransaction() {
    }

    public StockTransaction(User user, String symbol, String companyName, StockTransactionType type,
                           Integer quantity, BigDecimal pricePerShare, BigDecimal totalAmount,
                           LocalDateTime transactionDate) {
        this.user = user;
        this.symbol = symbol;
        this.companyName = companyName;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = totalAmount;
        this.transactionDate = transactionDate;
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

    public StockTransactionType getType() {
        return type;
    }

    public void setType(StockTransactionType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerShare() {
        return pricePerShare;
    }

    public void setPricePerShare(BigDecimal pricePerShare) {
        this.pricePerShare = pricePerShare;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
