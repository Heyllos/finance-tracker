package com.fintrack.service;

import com.fintrack.dto.TransactionRequest;
import com.fintrack.dto.TransactionResponse;
import com.fintrack.entity.Category;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import com.fintrack.entity.User;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.exception.UnauthorizedException;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TransactionResponse> getAllTransactions(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return transactionRepository.findByUserId(user.getId())
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByType(TransactionType type, Authentication authentication) {
        User user = getCurrentUser(authentication);
        return transactionRepository.findByUserIdAndType(user.getId(), type)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate, Authentication authentication) {
        User user = getCurrentUser(authentication);
        return transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate)
                .stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée avec l'ID: " + id));
        return new TransactionResponse(transaction);
    }

    public TransactionResponse createTransaction(TransactionRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setMerchant(request.getMerchant());
        transaction.setReceiptUrl(request.getReceiptUrl());
        transaction.setIsRecurring(request.getIsRecurring());
        transaction.setRecurrenceFrequency(request.getRecurrenceFrequency());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(savedTransaction);
    }

    public TransactionResponse updateTransaction(Long id, TransactionRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée avec l'ID: " + id));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setCategory(category);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setMerchant(request.getMerchant());
        transaction.setReceiptUrl(request.getReceiptUrl());
        transaction.setIsRecurring(request.getIsRecurring());
        transaction.setRecurrenceFrequency(request.getRecurrenceFrequency());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(updatedTransaction);
    }

    public void deleteTransaction(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction non trouvée avec l'ID: " + id));
        transactionRepository.delete(transaction);
    }

    public BigDecimal getTotalByType(TransactionType type, Authentication authentication) {
        User user = getCurrentUser(authentication);
        BigDecimal total = transactionRepository.sumAmountByUserIdAndType(user.getId(), type);
        return total != null ? total : BigDecimal.ZERO;
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }
}
