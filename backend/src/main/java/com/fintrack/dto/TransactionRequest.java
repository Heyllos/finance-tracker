package com.fintrack.dto;

import com.fintrack.entity.RecurrenceFrequency;
import com.fintrack.entity.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour créer ou modifier une transaction.
 */
public class TransactionRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 10 chiffres entiers et 2 décimales")
    private BigDecimal amount;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 2, max = 255, message = "La description doit contenir entre 2 et 255 caractères")
    private String description;

    @Size(max = 1000, message = "Les notes ne doivent pas dépasser 1000 caractères")
    private String notes;

    @NotNull(message = "La date de transaction est obligatoire")
    private LocalDate transactionDate;

    @NotNull(message = "Le type est obligatoire")
    private TransactionType type;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;

    @Size(max = 50)
    private String paymentMethod;

    @Size(max = 100)
    private String merchant;

    @Size(max = 255)
    private String receiptUrl;

    private Boolean isRecurring = false;

    private RecurrenceFrequency recurrenceFrequency;

    // Constructeurs
    public TransactionRequest() {
    }

    // Getters et Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
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
}
