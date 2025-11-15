package com.fintrack.service;

import com.fintrack.dto.*;
import com.fintrack.entity.*;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.PortfolioPositionRepository;
import com.fintrack.repository.StockTransactionRepository;
import com.fintrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioService {

    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @Autowired
    private PortfolioPositionRepository portfolioPositionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockService stockService;

    /**
     * Créer une nouvelle transaction d'actions (achat ou vente).
     */
    public StockTransactionResponse createTransaction(StockTransactionRequest request, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        // Calculer le montant total
        BigDecimal totalAmount = request.getPricePerShare()
            .multiply(new BigDecimal(request.getQuantity()))
            .setScale(2, RoundingMode.HALF_UP);

        if (request.getFees() != null) {
            totalAmount = totalAmount.add(request.getFees());
        }

        // Créer la transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setUser(user);
        transaction.setSymbol(request.getSymbol().toUpperCase());
        transaction.setCompanyName(request.getCompanyName());
        transaction.setType(request.getType());
        transaction.setQuantity(request.getQuantity());
        transaction.setPricePerShare(request.getPricePerShare());
        transaction.setTotalAmount(totalAmount);
        transaction.setFees(request.getFees());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNotes(request.getNotes());

        // Vérifier si c'est une vente
        if (request.getType() == StockTransactionType.SELL) {
            validateSellTransaction(user, request.getSymbol(), request.getQuantity());
        }

        // Sauvegarder la transaction
        StockTransaction savedTransaction = stockTransactionRepository.save(transaction);

        // Mettre à jour la position du portefeuille
        updatePortfolioPosition(user, request.getSymbol());

        return mapToTransactionResponse(savedTransaction);
    }

    /**
     * Obtenir toutes les positions du portefeuille.
     */
    public List<PortfolioPositionResponse> getPortfolio(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<PortfolioPosition> positions = portfolioPositionRepository.findByUserOrderByCurrentValueDesc(user);
        
        return positions.stream()
            .map(this::mapToPositionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtenir le résumé du portefeuille avec statistiques.
     */
    public PortfolioSummaryResponse getPortfolioSummary(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<PortfolioPosition> positions = portfolioPositionRepository.findByUserOrderByCurrentValueDesc(user);

        PortfolioSummaryResponse summary = new PortfolioSummaryResponse();
        
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (PortfolioPosition position : positions) {
            if (position.getTotalQuantity() > 0) {
                totalInvested = totalInvested.add(position.getTotalInvested());
                if (position.getCurrentValue() != null) {
                    currentValue = currentValue.add(position.getCurrentValue());
                }
            }
        }

        summary.setTotalInvested(totalInvested);
        summary.setCurrentValue(currentValue);
        summary.setTotalGainLoss(currentValue.subtract(totalInvested));
        
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            summary.setTotalGainLossPercent(
                summary.getTotalGainLoss()
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
            );
        }

        summary.setNumberOfPositions((int) positions.stream()
            .filter(p -> p.getTotalQuantity() > 0)
            .count());

        summary.setPositions(positions.stream()
            .filter(p -> p.getTotalQuantity() > 0)
            .map(this::mapToPositionResponse)
            .collect(Collectors.toList()));

        // Top gainers
        summary.setTopGainers(positions.stream()
            .filter(p -> p.getGainLossPercent() != null && p.getGainLossPercent().compareTo(BigDecimal.ZERO) > 0)
            .sorted(Comparator.comparing(PortfolioPosition::getGainLossPercent).reversed())
            .limit(3)
            .map(p -> new PortfolioSummaryResponse.TopPerformer(
                p.getSymbol(), p.getCompanyName(), p.getGainLossPercent(), p.getGainLoss()))
            .collect(Collectors.toList()));

        // Top losers
        summary.setTopLosers(positions.stream()
            .filter(p -> p.getGainLossPercent() != null && p.getGainLossPercent().compareTo(BigDecimal.ZERO) < 0)
            .sorted(Comparator.comparing(PortfolioPosition::getGainLossPercent))
            .limit(3)
            .map(p -> new PortfolioSummaryResponse.TopPerformer(
                p.getSymbol(), p.getCompanyName(), p.getGainLossPercent(), p.getGainLoss()))
            .collect(Collectors.toList()));

        return summary;
    }

    /**
     * Obtenir l'historique des transactions.
     */
    public List<StockTransactionResponse> getTransactionHistory(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<StockTransaction> transactions = stockTransactionRepository.findByUserOrderByTransactionDateDesc(user);
        
        return transactions.stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtenir une position spécifique.
     */
    public PortfolioPositionResponse getPosition(String symbol, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        PortfolioPosition position = portfolioPositionRepository.findByUserAndSymbol(user, symbol.toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Position non trouvée pour le symbole: " + symbol));
        
        return mapToPositionResponse(position);
    }

    /**
     * Obtenir les transactions pour un symbole spécifique.
     */
    public List<StockTransactionResponse> getTransactionsBySymbol(String symbol, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<StockTransaction> transactions = stockTransactionRepository
            .findByUserAndSymbolOrderByTransactionDateDesc(user, symbol.toUpperCase());
        
        return transactions.stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.toList());
    }

    /**
     * Actualiser les prix actuels de toutes les positions.
     */
    public void refreshPrices(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<PortfolioPosition> positions = portfolioPositionRepository.findByUserOrderBySymbolAsc(user);

        for (PortfolioPosition position : positions) {
            if (position.getTotalQuantity() > 0) {
                try {
                    // Récupérer le prix actuel via StockService
                    StockDataResponse stockData = stockService.getDailyStockData(position.getSymbol());
                    if (stockData != null && stockData.getTimeSeries() != null && !stockData.getTimeSeries().isEmpty()) {
                        BigDecimal currentPrice = stockData.getTimeSeries().get(0).getClose();
                        position.updateCurrentMetrics(currentPrice);
                        portfolioPositionRepository.save(position);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour du prix pour " + position.getSymbol() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Mettre à jour la position du portefeuille après une transaction.
     */
    private void updatePortfolioPosition(User user, String symbol) {
        List<StockTransaction> transactions = stockTransactionRepository
            .findByUserAndSymbolOrderByTransactionDateDesc(user, symbol.toUpperCase());

        int totalQuantity = 0;
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalShares = BigDecimal.ZERO;

        for (StockTransaction transaction : transactions) {
            if (transaction.getType() == StockTransactionType.BUY) {
                totalQuantity += transaction.getQuantity();
                totalInvested = totalInvested.add(transaction.getTotalAmount());
                totalShares = totalShares.add(
                    transaction.getPricePerShare().multiply(new BigDecimal(transaction.getQuantity()))
                );
            } else if (transaction.getType() == StockTransactionType.SELL) {
                totalQuantity -= transaction.getQuantity();
                // Réduire proportionnellement le montant investi
                if (totalQuantity > 0) {
                    BigDecimal sellRatio = new BigDecimal(transaction.getQuantity())
                        .divide(new BigDecimal(totalQuantity + transaction.getQuantity()), 4, RoundingMode.HALF_UP);
                    totalInvested = totalInvested.multiply(BigDecimal.ONE.subtract(sellRatio));
                } else {
                    totalInvested = BigDecimal.ZERO;
                }
            }
        }

        if (totalQuantity > 0) {
            BigDecimal averageBuyPrice = totalInvested.divide(new BigDecimal(totalQuantity), 4, RoundingMode.HALF_UP);

            PortfolioPosition position = portfolioPositionRepository
                .findByUserAndSymbol(user, symbol.toUpperCase())
                .orElse(new PortfolioPosition());

            position.setUser(user);
            position.setSymbol(symbol.toUpperCase());
            position.setCompanyName(transactions.get(0).getCompanyName());
            position.setTotalQuantity(totalQuantity);
            position.setAverageBuyPrice(averageBuyPrice);
            position.setTotalInvested(totalInvested);

            portfolioPositionRepository.save(position);
        } else {
            // Si la quantité est 0, supprimer la position
            portfolioPositionRepository.findByUserAndSymbol(user, symbol.toUpperCase())
                .ifPresent(position -> portfolioPositionRepository.delete(position));
        }
    }

    /**
     * Valider une transaction de vente.
     */
    private void validateSellTransaction(User user, String symbol, Integer quantity) {
        PortfolioPosition position = portfolioPositionRepository
            .findByUserAndSymbol(user, symbol.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Vous ne possédez aucune action " + symbol));

        if (position.getTotalQuantity() < quantity) {
            throw new IllegalArgumentException(
                "Quantité insuffisante. Vous possédez " + position.getTotalQuantity() + 
                " actions, vous essayez d'en vendre " + quantity
            );
        }
    }

    /**
     * Récupérer l'utilisateur depuis l'authentification.
     */
    private User getUserFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));
    }

    /**
     * Mapper StockTransaction vers StockTransactionResponse.
     */
    private StockTransactionResponse mapToTransactionResponse(StockTransaction transaction) {
        StockTransactionResponse response = new StockTransactionResponse();
        response.setId(transaction.getId());
        response.setSymbol(transaction.getSymbol());
        response.setCompanyName(transaction.getCompanyName());
        response.setType(transaction.getType());
        response.setQuantity(transaction.getQuantity());
        response.setPricePerShare(transaction.getPricePerShare());
        response.setTotalAmount(transaction.getTotalAmount());
        response.setFees(transaction.getFees());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    /**
     * Mapper PortfolioPosition vers PortfolioPositionResponse.
     */
    private PortfolioPositionResponse mapToPositionResponse(PortfolioPosition position) {
        PortfolioPositionResponse response = new PortfolioPositionResponse();
        response.setId(position.getId());
        response.setSymbol(position.getSymbol());
        response.setCompanyName(position.getCompanyName());
        response.setTotalQuantity(position.getTotalQuantity());
        response.setAverageBuyPrice(position.getAverageBuyPrice());
        response.setTotalInvested(position.getTotalInvested());
        response.setCurrentPrice(position.getCurrentPrice());
        response.setCurrentValue(position.getCurrentValue());
        response.setGainLoss(position.getGainLoss());
        response.setGainLossPercent(position.getGainLossPercent());
        response.setUpdatedAt(position.getUpdatedAt());
        return response;
    }
}
