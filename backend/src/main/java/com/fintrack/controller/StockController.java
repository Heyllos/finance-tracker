package com.fintrack.controller;

import com.fintrack.dto.StockDataResponse;
import com.fintrack.service.StockService;
import com.fintrack.service.YahooFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour les données boursières.
 */
@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired
    private StockService stockService;
    
    @Autowired
    private YahooFinanceService yahooFinanceService;

    /**
     * Récupère les données intraday pour un symbole boursier.
     * 
     * GET /api/stocks/{symbol}?interval=15min
     * 
     * @param symbol Symbole boursier (ex: AAPL, TSLA, MSFT)
     * @param interval Intervalle (1min, 5min, 15min, 30min, 60min)
     * @return Données boursières
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "15min") String interval) {
        try {
            StockDataResponse data = stockService.getStockData(symbol, interval);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Paramètre invalide", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Erreur API", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Récupère les données quotidiennes pour un symbole boursier.
     * 
     * GET /api/stocks/{symbol}/daily
     * 
     * @param symbol Symbole boursier
     * @return Données boursières quotidiennes
     */
    @GetMapping("/{symbol}/daily")
    public ResponseEntity<?> getDailyStockData(@PathVariable String symbol) {
        try {
            StockDataResponse data = stockService.getDailyStockData(symbol);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Paramètre invalide", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Erreur API", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Récupère les informations fondamentales d'une entreprise.
     * 
     * GET /api/stocks/{symbol}/overview
     * 
     * @param symbol Symbole boursier
     * @return Informations fondamentales
     */
    @GetMapping("/{symbol}/overview")
    public ResponseEntity<?> getCompanyOverview(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(stockService.getCompanyOverview(symbol));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse("Paramètre invalide", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Erreur API", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", "Une erreur inattendue s'est produite"));
        }
    }

    /**
     * Récupère l'indicateur RSI.
     * 
     * GET /api/stocks/{symbol}/rsi?interval=daily&time_period=14
     * 
     * @param symbol Symbole boursier
     * @param interval Intervalle
     * @param timePeriod Période (défaut: 14)
     * @return RSI
     */
    @GetMapping("/{symbol}/rsi")
    public ResponseEntity<?> getRSI(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam(defaultValue = "14") int timePeriod) {
        try {
            // Utiliser Yahoo Finance pour économiser les requêtes Alpha Vantage
            return ResponseEntity.ok(yahooFinanceService.calculateRSI(symbol, timePeriod));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Erreur API", e.getMessage()));
        }
    }

    /**
     * Récupère l'indicateur MACD.
     * 
     * GET /api/stocks/{symbol}/macd?interval=daily
     * 
     * @param symbol Symbole boursier
     * @param interval Intervalle
     * @return MACD
     */
    @GetMapping("/{symbol}/macd")
    public ResponseEntity<?> getMACD(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "daily") String interval) {
        try {
            // Utiliser Yahoo Finance pour économiser les requêtes Alpha Vantage
            return ResponseEntity.ok(yahooFinanceService.calculateMACD(symbol));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(createErrorResponse("Erreur API", e.getMessage()));
        }
    }

    /**
     * Crée une réponse d'erreur formatée.
     */
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return errorResponse;
    }
}
