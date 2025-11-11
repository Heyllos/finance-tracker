package com.fintrack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO pour les taux de change Forex.
 */
public class ExchangeRateResponse {
    
    private String base;
    private LocalDateTime timestamp;
    private Map<String, BigDecimal> rates;

    public ExchangeRateResponse() {
    }

    public ExchangeRateResponse(String base, LocalDateTime timestamp, Map<String, BigDecimal> rates) {
        this.base = base;
        this.timestamp = timestamp;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
