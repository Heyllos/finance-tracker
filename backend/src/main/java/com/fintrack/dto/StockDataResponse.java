package com.fintrack.dto;

import java.util.List;

/**
 * DTO pour la réponse contenant les données boursières.
 */
public class StockDataResponse {
    
    private String symbol;
    private String interval;
    private String lastRefreshed;
    private List<StockDataPoint> timeSeries;
    private StockMetadata metadata;
    private Boolean isDemo;

    public StockDataResponse() {
    }

    public StockDataResponse(String symbol, String interval, String lastRefreshed, 
                            List<StockDataPoint> timeSeries) {
        this.symbol = symbol;
        this.interval = interval;
        this.lastRefreshed = lastRefreshed;
        this.timeSeries = timeSeries;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public List<StockDataPoint> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<StockDataPoint> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public StockMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(StockMetadata metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsDemo() {
        return isDemo;
    }

    public void setIsDemo(Boolean isDemo) {
        this.isDemo = isDemo;
    }

    /**
     * Classe interne pour les métadonnées.
     */
    public static class StockMetadata {
        private String information;
        private String symbol;
        private String lastRefreshed;
        private String interval;
        private String outputSize;
        private String timeZone;

        public StockMetadata() {
        }

        public String getInformation() {
            return information;
        }

        public void setInformation(String information) {
            this.information = information;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getLastRefreshed() {
            return lastRefreshed;
        }

        public void setLastRefreshed(String lastRefreshed) {
            this.lastRefreshed = lastRefreshed;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getOutputSize() {
            return outputSize;
        }

        public void setOutputSize(String outputSize) {
            this.outputSize = outputSize;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }
    }
}
