package com.fintrack.controller;

import com.fintrack.dto.CryptoPriceResponse;
import com.fintrack.dto.ExchangeRateResponse;
import com.fintrack.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour les API externes (Forex et Crypto).
 */
@RestController
@RequestMapping("/api/external")
@CrossOrigin(origins = "http://localhost:4200")
public class ExternalApiController {

    @Autowired
    private ExternalApiService externalApiService;

    /**
     * Récupère les taux de change pour une devise de base.
     * GET /api/external/rates?base=USD
     * 
     * @param base Devise de base (défaut: USD)
     * @return Taux de change
     */
    @GetMapping("/rates")
    public ResponseEntity<ExchangeRateResponse> getExchangeRates(
            @RequestParam(defaultValue = "USD") String base) {
        return ResponseEntity.ok(externalApiService.getExchangeRates(base));
    }

    /**
     * Récupère le taux de change entre deux devises.
     * GET /api/external/rates/convert?base=USD&target=EUR
     * 
     * @param base Devise de base
     * @param target Devise cible
     * @return Taux de change
     */
    @GetMapping("/rates/convert")
    public ResponseEntity<ExchangeRateResponse> convertCurrency(
            @RequestParam String base,
            @RequestParam String target) {
        return ResponseEntity.ok(externalApiService.getExchangeRates(base, target));
    }

    /**
     * Récupère les prix des principales crypto-monnaies.
     * GET /api/external/crypto
     * 
     * @return Liste des prix des cryptos
     */
    @GetMapping("/crypto")
    public ResponseEntity<List<CryptoPriceResponse>> getCryptoPrices() {
        return ResponseEntity.ok(externalApiService.getCryptoPrices());
    }
}
