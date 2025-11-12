package com.fintrack.dto;

import java.math.BigDecimal;

/**
 * DTO pour les informations fondamentales d'une entreprise.
 */
public class CompanyOverview {
    
    private String symbol;
    private String name;
    private String description;
    private String sector;
    private String industry;
    private String country;
    private String exchange;
    private String currency;
    
    // Métriques financières
    private BigDecimal marketCapitalization;
    private BigDecimal peRatio;
    private BigDecimal pegRatio;
    private BigDecimal bookValue;
    private BigDecimal dividendPerShare;
    private BigDecimal dividendYield;
    private BigDecimal eps;
    private BigDecimal revenuePerShareTTM;
    private BigDecimal profitMargin;
    private BigDecimal operatingMarginTTM;
    private BigDecimal returnOnAssetsTTM;
    private BigDecimal returnOnEquityTTM;
    private BigDecimal revenueTTM;
    private BigDecimal grossProfitTTM;
    private BigDecimal ebitda;
    
    // Métriques de trading
    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal fiftyTwoWeekLow;
    private BigDecimal fiftyDayMovingAverage;
    private BigDecimal twoHundredDayMovingAverage;
    private Long sharesOutstanding;
    private String exDividendDate;
    
    // Constructeurs
    public CompanyOverview() {
    }

    // Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(BigDecimal marketCapitalization) {
        this.marketCapitalization = marketCapitalization;
    }

    public BigDecimal getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }

    public BigDecimal getPegRatio() {
        return pegRatio;
    }

    public void setPegRatio(BigDecimal pegRatio) {
        this.pegRatio = pegRatio;
    }

    public BigDecimal getBookValue() {
        return bookValue;
    }

    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue;
    }

    public BigDecimal getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(BigDecimal dividendPerShare) {
        this.dividendPerShare = dividendPerShare;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public BigDecimal getEps() {
        return eps;
    }

    public void setEps(BigDecimal eps) {
        this.eps = eps;
    }

    public BigDecimal getRevenuePerShareTTM() {
        return revenuePerShareTTM;
    }

    public void setRevenuePerShareTTM(BigDecimal revenuePerShareTTM) {
        this.revenuePerShareTTM = revenuePerShareTTM;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal getOperatingMarginTTM() {
        return operatingMarginTTM;
    }

    public void setOperatingMarginTTM(BigDecimal operatingMarginTTM) {
        this.operatingMarginTTM = operatingMarginTTM;
    }

    public BigDecimal getReturnOnAssetsTTM() {
        return returnOnAssetsTTM;
    }

    public void setReturnOnAssetsTTM(BigDecimal returnOnAssetsTTM) {
        this.returnOnAssetsTTM = returnOnAssetsTTM;
    }

    public BigDecimal getReturnOnEquityTTM() {
        return returnOnEquityTTM;
    }

    public void setReturnOnEquityTTM(BigDecimal returnOnEquityTTM) {
        this.returnOnEquityTTM = returnOnEquityTTM;
    }

    public BigDecimal getRevenueTTM() {
        return revenueTTM;
    }

    public void setRevenueTTM(BigDecimal revenueTTM) {
        this.revenueTTM = revenueTTM;
    }

    public BigDecimal getGrossProfitTTM() {
        return grossProfitTTM;
    }

    public void setGrossProfitTTM(BigDecimal grossProfitTTM) {
        this.grossProfitTTM = grossProfitTTM;
    }

    public BigDecimal getEbitda() {
        return ebitda;
    }

    public void setEbitda(BigDecimal ebitda) {
        this.ebitda = ebitda;
    }

    public BigDecimal getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }

    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    public BigDecimal getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }

    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    public BigDecimal getFiftyDayMovingAverage() {
        return fiftyDayMovingAverage;
    }

    public void setFiftyDayMovingAverage(BigDecimal fiftyDayMovingAverage) {
        this.fiftyDayMovingAverage = fiftyDayMovingAverage;
    }

    public BigDecimal getTwoHundredDayMovingAverage() {
        return twoHundredDayMovingAverage;
    }

    public void setTwoHundredDayMovingAverage(BigDecimal twoHundredDayMovingAverage) {
        this.twoHundredDayMovingAverage = twoHundredDayMovingAverage;
    }

    public Long getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(Long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }
}
