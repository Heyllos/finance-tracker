package com.fintrack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour le prix d'une crypto-monnaie.
 */
public class CryptoPriceResponse {
    
    private String symbol;
    private String name;
    private BigDecimal priceUsd;
    private BigDecimal priceEur;
    private BigDecimal changePercent24h;
    private BigDecimal marketCapUsd;
    private BigDecimal volume24h;
    private LocalDateTime timestamp;

    public CryptoPriceResponse() {
    }

    public CryptoPriceResponse(String symbol, String name, BigDecimal priceUsd, BigDecimal priceEur) {
        this.symbol = symbol;
        this.name = name;
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.timestamp = LocalDateTime.now();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public BigDecimal getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(BigDecimal priceEur) {
        this.priceEur = priceEur;
    }

    public BigDecimal getChangePercent24h() {
        return changePercent24h;
    }

    public void setChangePercent24h(BigDecimal changePercent24h) {
        this.changePercent24h = changePercent24h;
    }

    public BigDecimal getMarketCapUsd() {
        return marketCapUsd;
    }

    public void setMarketCapUsd(BigDecimal marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
