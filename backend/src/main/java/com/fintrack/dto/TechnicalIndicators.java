package com.fintrack.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les indicateurs techniques calculés.
 */
public class TechnicalIndicators {
    
    private String symbol;
    private String indicator;
    private String interval;
    private String lastRefreshed;
    
    // RSI
    private BigDecimal rsi;
    
    // MACD
    private BigDecimal macd;
    private BigDecimal macdSignal;
    private BigDecimal macdHist;
    
    // SMA (Simple Moving Average)
    private Map<String, BigDecimal> sma; // ex: {"50": 150.25, "200": 145.50}
    
    // EMA (Exponential Moving Average)
    private Map<String, BigDecimal> ema;
    
    // Stochastic
    private BigDecimal stochK;
    private BigDecimal stochD;
    
    // ADX (Average Directional Index)
    private BigDecimal adx;
    
    // Bollinger Bands
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerMiddle;
    private BigDecimal bollingerLower;

    public TechnicalIndicators() {
    }

    // Getters et Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public BigDecimal getRsi() {
        return rsi;
    }

    public void setRsi(BigDecimal rsi) {
        this.rsi = rsi;
    }

    public BigDecimal getMacd() {
        return macd;
    }

    public void setMacd(BigDecimal macd) {
        this.macd = macd;
    }

    public BigDecimal getMacdSignal() {
        return macdSignal;
    }

    public void setMacdSignal(BigDecimal macdSignal) {
        this.macdSignal = macdSignal;
    }

    public BigDecimal getMacdHist() {
        return macdHist;
    }

    public void setMacdHist(BigDecimal macdHist) {
        this.macdHist = macdHist;
    }

    public Map<String, BigDecimal> getSma() {
        return sma;
    }

    public void setSma(Map<String, BigDecimal> sma) {
        this.sma = sma;
    }

    public Map<String, BigDecimal> getEma() {
        return ema;
    }

    public void setEma(Map<String, BigDecimal> ema) {
        this.ema = ema;
    }

    public BigDecimal getStochK() {
        return stochK;
    }

    public void setStochK(BigDecimal stochK) {
        this.stochK = stochK;
    }

    public BigDecimal getStochD() {
        return stochD;
    }

    public void setStochD(BigDecimal stochD) {
        this.stochD = stochD;
    }

    public BigDecimal getAdx() {
        return adx;
    }

    public void setAdx(BigDecimal adx) {
        this.adx = adx;
    }

    public BigDecimal getBollingerUpper() {
        return bollingerUpper;
    }

    public void setBollingerUpper(BigDecimal bollingerUpper) {
        this.bollingerUpper = bollingerUpper;
    }

    public BigDecimal getBollingerMiddle() {
        return bollingerMiddle;
    }

    public void setBollingerMiddle(BigDecimal bollingerMiddle) {
        this.bollingerMiddle = bollingerMiddle;
    }

    public BigDecimal getBollingerLower() {
        return bollingerLower;
    }

    public void setBollingerLower(BigDecimal bollingerLower) {
        this.bollingerLower = bollingerLower;
    }
}
