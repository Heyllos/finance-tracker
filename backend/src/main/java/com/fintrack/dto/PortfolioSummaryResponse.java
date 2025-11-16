package com.fintrack.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PortfolioSummaryResponse {

    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercent;
    private Integer numberOfPositions;
    private List<PortfolioPositionResponse> positions = new ArrayList<>();
    private List<TopPerformer> topGainers = new ArrayList<>();
    private List<TopPerformer> topLosers = new ArrayList<>();

    // Classe interne pour les top performers
    public static class TopPerformer {
        private String symbol;
        private String companyName;
        private BigDecimal gainLossPercent;
        private BigDecimal gainLoss;

        public TopPerformer() {
        }

        public TopPerformer(String symbol, String companyName, BigDecimal gainLossPercent, BigDecimal gainLoss) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.gainLossPercent = gainLossPercent;
            this.gainLoss = gainLoss;
        }

        // Getters et Setters
        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public BigDecimal getGainLossPercent() {
            return gainLossPercent;
        }

        public void setGainLossPercent(BigDecimal gainLossPercent) {
            this.gainLossPercent = gainLossPercent;
        }

        public BigDecimal getGainLoss() {
            return gainLoss;
        }

        public void setGainLoss(BigDecimal gainLoss) {
            this.gainLoss = gainLoss;
        }
    }

    // Constructeurs
    public PortfolioSummaryResponse() {
        this.totalInvested = BigDecimal.ZERO;
        this.currentValue = BigDecimal.ZERO;
        this.totalGainLoss = BigDecimal.ZERO;
        this.totalGainLossPercent = BigDecimal.ZERO;
        this.numberOfPositions = 0;
    }

    // Getters et Setters
    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public void setTotalGainLoss(BigDecimal totalGainLoss) {
        this.totalGainLoss = totalGainLoss;
    }

    public BigDecimal getTotalGainLossPercent() {
        return totalGainLossPercent;
    }

    public void setTotalGainLossPercent(BigDecimal totalGainLossPercent) {
        this.totalGainLossPercent = totalGainLossPercent;
    }

    public Integer getNumberOfPositions() {
        return numberOfPositions;
    }

    public void setNumberOfPositions(Integer numberOfPositions) {
        this.numberOfPositions = numberOfPositions;
    }

    public List<PortfolioPositionResponse> getPositions() {
        return positions;
    }

    public void setPositions(List<PortfolioPositionResponse> positions) {
        this.positions = positions;
    }

    public List<TopPerformer> getTopGainers() {
        return topGainers;
    }

    public void setTopGainers(List<TopPerformer> topGainers) {
        this.topGainers = topGainers;
    }

    public List<TopPerformer> getTopLosers() {
        return topLosers;
    }

    public void setTopLosers(List<TopPerformer> topLosers) {
        this.topLosers = topLosers;
    }
}
