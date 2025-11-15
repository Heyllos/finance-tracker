package com.fintrack.controller;

import com.fintrack.dto.*;
import com.fintrack.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion du portefeuille d'actions.
 */
@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    /**
     * Créer une nouvelle transaction d'actions (achat ou vente).
     * 
     * POST /api/portfolio/transactions
     * 
     * @param request Données de la transaction
     * @param authentication Authentification de l'utilisateur
     * @return Transaction créée
     */
    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(
            @Valid @RequestBody StockTransactionRequest request,
            Authentication authentication) {
        try {
            StockTransactionResponse transaction = portfolioService.createTransaction(request, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Erreur de validation", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Obtenir toutes les positions du portefeuille.
     * 
     * GET /api/portfolio/positions
     * 
     * @param authentication Authentification de l'utilisateur
     * @return Liste des positions
     */
    @GetMapping("/positions")
    public ResponseEntity<?> getPortfolio(Authentication authentication) {
        try {
            List<PortfolioPositionResponse> positions = portfolioService.getPortfolio(authentication);
            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Obtenir le résumé du portefeuille avec statistiques.
     * 
     * GET /api/portfolio/summary
     * 
     * @param authentication Authentification de l'utilisateur
     * @return Résumé du portefeuille
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getPortfolioSummary(Authentication authentication) {
        try {
            PortfolioSummaryResponse summary = portfolioService.getPortfolioSummary(authentication);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Obtenir l'historique de toutes les transactions.
     * 
     * GET /api/portfolio/transactions
     * 
     * @param authentication Authentification de l'utilisateur
     * @return Liste des transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionHistory(Authentication authentication) {
        try {
            List<StockTransactionResponse> transactions = portfolioService.getTransactionHistory(authentication);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Obtenir une position spécifique par symbole.
     * 
     * GET /api/portfolio/positions/{symbol}
     * 
     * @param symbol Symbole de l'action
     * @param authentication Authentification de l'utilisateur
     * @return Position pour le symbole
     */
    @GetMapping("/positions/{symbol}")
    public ResponseEntity<?> getPosition(
            @PathVariable String symbol,
            Authentication authentication) {
        try {
            PortfolioPositionResponse position = portfolioService.getPosition(symbol, authentication);
            return ResponseEntity.ok(position);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Position non trouvée", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Obtenir les transactions pour un symbole spécifique.
     * 
     * GET /api/portfolio/transactions/{symbol}
     * 
     * @param symbol Symbole de l'action
     * @param authentication Authentification de l'utilisateur
     * @return Liste des transactions pour le symbole
     */
    @GetMapping("/transactions/{symbol}")
    public ResponseEntity<?> getTransactionsBySymbol(
            @PathVariable String symbol,
            Authentication authentication) {
        try {
            List<StockTransactionResponse> transactions = 
                portfolioService.getTransactionsBySymbol(symbol, authentication);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Actualiser les prix actuels de toutes les positions.
     * 
     * PUT /api/portfolio/refresh-prices
     * 
     * @param authentication Authentification de l'utilisateur
     * @return Message de confirmation
     */
    @PutMapping("/refresh-prices")
    public ResponseEntity<?> refreshPrices(Authentication authentication) {
        try {
            portfolioService.refreshPrices(authentication);
            return ResponseEntity.ok(Map.of(
                "message", "Prix actualisés avec succès",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur serveur", "Erreur lors de l'actualisation des prix"));
        }
    }

    /**
     * Créer une réponse d'erreur standardisée.
     */
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
