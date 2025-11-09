package com.fintrack.service;

import com.fintrack.dto.BudgetRequest;
import com.fintrack.dto.BudgetResponse;
import com.fintrack.entity.Budget;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.exception.UnauthorizedException;
import com.fintrack.repository.BudgetRepository;
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
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BudgetResponse> getAllBudgets(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        
        // Mettre à jour le montant dépensé pour chaque budget
        budgets.forEach(this::updateSpentAmount);
        
        return budgets.stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
    }

    public List<BudgetResponse> getActiveBudgets(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActive(user.getId(), true);
        
        // Mettre à jour le montant dépensé pour chaque budget
        budgets.forEach(this::updateSpentAmount);
        
        return budgets.stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
    }

    public List<BudgetResponse> getActiveBudgetsForDate(LocalDate date, Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Budget> budgets = budgetRepository.findActiveBudgetsByUserIdAndDate(user.getId(), date);
        
        // Mettre à jour le montant dépensé pour chaque budget
        budgets.forEach(this::updateSpentAmount);
        
        return budgets.stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
    }

    public BudgetResponse getBudgetById(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget non trouvé avec l'ID: " + id));
        
        // Mettre à jour le montant dépensé
        updateSpentAmount(budget);
        
        return new BudgetResponse(budget);
    }

    public BudgetResponse createBudget(BudgetRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));

        Budget budget = new Budget();
        budget.setName(request.getName());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setUser(user);
        budget.setCategory(category);
        budget.setIsRecurring(request.getIsRecurring());
        budget.setRecurrenceFrequency(request.getRecurrenceFrequency());
        budget.setAlertEnabled(request.getAlertEnabled());
        budget.setAlertThreshold(request.getAlertThreshold());

        Budget savedBudget = budgetRepository.save(budget);
        return new BudgetResponse(savedBudget);
    }

    public BudgetResponse updateBudget(Long id, BudgetRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget non trouvé avec l'ID: " + id));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));

        budget.setName(request.getName());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setCategory(category);
        budget.setIsRecurring(request.getIsRecurring());
        budget.setRecurrenceFrequency(request.getRecurrenceFrequency());
        budget.setAlertEnabled(request.getAlertEnabled());
        budget.setAlertThreshold(request.getAlertThreshold());

        Budget updatedBudget = budgetRepository.save(budget);
        return new BudgetResponse(updatedBudget);
    }

    public void deleteBudget(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget non trouvé avec l'ID: " + id));
        budgetRepository.delete(budget);
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));
    }

    /**
     * Met à jour le montant dépensé d'un budget en calculant la somme des transactions
     * de la catégorie du budget pendant la période du budget.
     */
    private void updateSpentAmount(Budget budget) {
        BigDecimal spent = transactionRepository.sumAmountByCategoryIdAndDateBetween(
                budget.getCategory().getId(),
                budget.getStartDate(),
                budget.getEndDate()
        );
        
        // Si aucune transaction, le montant est 0
        budget.setSpentAmount(spent != null ? spent : BigDecimal.ZERO);
    }
}
