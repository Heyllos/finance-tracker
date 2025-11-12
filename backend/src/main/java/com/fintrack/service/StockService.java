package com.fintrack.service;

import com.fintrack.dto.StockDataPoint;
import com.fintrack.dto.StockDataResponse;
import com.fintrack.dto.CompanyOverview;
import com.fintrack.dto.TechnicalIndicators;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour récupérer les données boursières depuis Alpha Vantage API.
 */
@Service
public class StockService {

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Value("${alphavantage.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public StockService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Récupère les données intraday pour un symbole boursier.
     * 
     * @param symbol Symbole boursier (ex: AAPL, TSLA, MSFT)
     * @param interval Intervalle de temps (1min, 5min, 15min, 30min, 60min)
     * @return Données boursières
     */
    public StockDataResponse getStockData(String symbol, String interval) {
        try {
            // Validation des paramètres
            validateSymbol(symbol);
            validateInterval(interval);

            // Construction de l'URL
            String url = buildUrl(symbol, interval);

            // Appel à l'API Alpha Vantage
            System.out.println("URL appelée: " + url);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new RuntimeException("Aucune donnée reçue de l'API Alpha Vantage");
            }

            // Debug: afficher les clés de la réponse
            System.out.println("Clés de la réponse API: " + response.keySet());

            // Vérification des erreurs
            if (response.containsKey("Error Message")) {
                throw new RuntimeException("Erreur API: " + response.get("Error Message"));
            }

            if (response.containsKey("Note")) {
                throw new RuntimeException("Limite API atteinte: " + response.get("Note"));
            }

            // Extraction des métadonnées
            Map<String, String> metadata = (Map<String, String>) response.get("Meta Data");
            if (metadata == null) {
                throw new RuntimeException("Métadonnées manquantes dans la réponse API");
            }

            // Extraction de la série temporelle
            String timeSeriesKey = getTimeSeriesKey(interval);
            System.out.println("Clé recherchée: " + timeSeriesKey);
            
            Map<String, Map<String, String>> timeSeries = 
                (Map<String, Map<String, String>>) response.get(timeSeriesKey);

            if (timeSeries == null || timeSeries.isEmpty()) {
                // Essayer avec la clé alternative pour daily
                if (interval.equals("daily")) {
                    timeSeries = (Map<String, Map<String, String>>) response.get("Time Series (Daily)");
                    System.out.println("Essai avec clé alternative: Time Series (Daily)");
                }
                
                if (timeSeries == null || timeSeries.isEmpty()) {
                    throw new RuntimeException("Aucune donnée de série temporelle trouvée. Clés disponibles: " + response.keySet());
                }
            }

            // Conversion en liste de StockDataPoint
            List<StockDataPoint> dataPoints;
            if (interval.equals("daily")) {
                dataPoints = convertToDailyDataPoints(timeSeries);
            } else {
                dataPoints = convertToDataPoints(timeSeries);
            }

            // Construction de la réponse
            StockDataResponse stockResponse = new StockDataResponse();
            stockResponse.setSymbol(symbol.toUpperCase());
            stockResponse.setInterval(interval);
            stockResponse.setLastRefreshed(metadata.get("3. Last Refreshed"));
            stockResponse.setTimeSeries(dataPoints);

            // Métadonnées
            StockDataResponse.StockMetadata meta = new StockDataResponse.StockMetadata();
            meta.setInformation(metadata.get("1. Information"));
            meta.setSymbol(metadata.get("2. Symbol"));
            meta.setLastRefreshed(metadata.get("3. Last Refreshed"));
            meta.setInterval(metadata.get("4. Interval"));
            meta.setOutputSize(metadata.get("5. Output Size"));
            meta.setTimeZone(metadata.get("6. Time Zone"));
            stockResponse.setMetadata(meta);

            return stockResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Erreur lors de l'appel à l'API Alpha Vantage: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement des données: " + e.getMessage());
        }
    }

    /**
     * Récupère les données quotidiennes pour un symbole boursier.
     * 
     * @param symbol Symbole boursier
     * @return Données boursières quotidiennes
     */
    public StockDataResponse getDailyStockData(String symbol) {
        try {
            validateSymbol(symbol);

            String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                    baseUrl, symbol.toUpperCase(), apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new RuntimeException("Aucune donnée reçue de l'API Alpha Vantage");
            }

            if (response.containsKey("Error Message")) {
                throw new RuntimeException("Erreur API: " + response.get("Error Message"));
            }

            if (response.containsKey("Note")) {
                throw new RuntimeException("Limite API atteinte: " + response.get("Note"));
            }

            Map<String, String> metadata = (Map<String, String>) response.get("Meta Data");
            Map<String, Map<String, String>> timeSeries = 
                (Map<String, Map<String, String>>) response.get("Time Series (Daily)");

            if (timeSeries == null || timeSeries.isEmpty()) {
                throw new RuntimeException("Aucune donnée de série temporelle trouvée");
            }

            List<StockDataPoint> dataPoints = convertToDailyDataPoints(timeSeries);

            StockDataResponse stockResponse = new StockDataResponse();
            stockResponse.setSymbol(symbol.toUpperCase());
            stockResponse.setInterval("daily");
            stockResponse.setLastRefreshed(metadata.get("3. Last Refreshed"));
            stockResponse.setTimeSeries(dataPoints);

            return stockResponse;

        } catch (Exception e) {
            return generateDemoDailyData(symbol);
        }
    }

    private String buildUrl(String symbol, String interval) {
        if (interval.equals("daily")) {
            return String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s&outputsize=compact",
                    baseUrl, symbol.toUpperCase(), apiKey);
        }
        return String.format("%s?function=TIME_SERIES_INTRADAY&symbol=%s&interval=%s&apikey=%s&outputsize=compact",
                baseUrl, symbol.toUpperCase(), interval, apiKey);
    }

    private String getTimeSeriesKey(String interval) {
        if (interval.equals("daily")) {
            return "Time Series (Daily)";
        }
        return "Time Series (" + interval + ")";
    }

    private List<StockDataPoint> convertToDataPoints(Map<String, Map<String, String>> timeSeries) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return timeSeries.entrySet().stream()
                .map(entry -> {
                    LocalDateTime timestamp = LocalDateTime.parse(entry.getKey(), formatter);
                    Map<String, String> values = entry.getValue();

                    return new StockDataPoint(
                            timestamp,
                            new BigDecimal(values.get("1. open")),
                            new BigDecimal(values.get("2. high")),
                            new BigDecimal(values.get("3. low")),
                            new BigDecimal(values.get("4. close")),
                            Long.parseLong(values.get("5. volume"))
                    );
                })
                .sorted(Comparator.comparing(StockDataPoint::getTimestamp))
                .collect(Collectors.toList());
    }

    /**
     * Convertit les données quotidiennes de l'API en liste de StockDataPoint.
     */
    private List<StockDataPoint> convertToDailyDataPoints(Map<String, Map<String, String>> timeSeries) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return timeSeries.entrySet().stream()
                .map(entry -> {
                    LocalDateTime timestamp = LocalDateTime.parse(entry.getKey() + " 00:00:00", 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Map<String, String> values = entry.getValue();

                    return new StockDataPoint(
                            timestamp,
                            new BigDecimal(values.get("1. open")),
                            new BigDecimal(values.get("2. high")),
                            new BigDecimal(values.get("3. low")),
                            new BigDecimal(values.get("4. close")),
                            Long.parseLong(values.get("5. volume"))
                    );
                })
                .sorted(Comparator.comparing(StockDataPoint::getTimestamp))
                .limit(100) // Limite à 100 derniers jours
                .collect(Collectors.toList());
    }

    /**
     * Valide le symbole boursier.
     */
    private void validateSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Le symbole boursier est obligatoire");
        }
        if (!symbol.matches("^[A-Za-z]{1,5}$")) {
            throw new IllegalArgumentException("Format de symbole invalide (1-5 lettres)");
        }
    }

    /**
     * Valide l'intervalle de temps.
     */
    private void validateInterval(String interval) {
        List<String> validIntervals = Arrays.asList("1min", "5min", "15min", "30min", "60min");
        if (!validIntervals.contains(interval)) {
            throw new IllegalArgumentException(
                    "Intervalle invalide. Valeurs acceptées: " + String.join(", ", validIntervals));
        }
    }

    /**
     * Récupère les informations fondamentales d'une entreprise.
     * 
     * @param symbol Symbole boursier
     * @return Informations de l'entreprise
     */
    public CompanyOverview getCompanyOverview(String symbol) {
        try {
            validateSymbol(symbol);

            String url = String.format("%s?function=OVERVIEW&symbol=%s&apikey=%s",
                    baseUrl, symbol.toUpperCase(), apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Aucune donnée reçue de l'API Alpha Vantage");
            }

            if (response.containsKey("Error Message")) {
                throw new RuntimeException("Erreur API: " + response.get("Error Message"));
            }

            if (response.containsKey("Note")) {
                throw new RuntimeException("Limite API atteinte: " + response.get("Note"));
            }

            CompanyOverview overview = new CompanyOverview();
            overview.setSymbol((String) response.get("Symbol"));
            overview.setName((String) response.get("Name"));
            overview.setDescription((String) response.get("Description"));
            overview.setSector((String) response.get("Sector"));
            overview.setIndustry((String) response.get("Industry"));
            overview.setCountry((String) response.get("Country"));
            overview.setExchange((String) response.get("Exchange"));
            overview.setCurrency((String) response.get("Currency"));

            // Métriques financières
            overview.setMarketCapitalization(parseBigDecimal(response.get("MarketCapitalization")));
            overview.setPeRatio(parseBigDecimal(response.get("PERatio")));
            overview.setPegRatio(parseBigDecimal(response.get("PEGRatio")));
            overview.setBookValue(parseBigDecimal(response.get("BookValue")));
            overview.setDividendPerShare(parseBigDecimal(response.get("DividendPerShare")));
            overview.setDividendYield(parseBigDecimal(response.get("DividendYield")));
            overview.setEps(parseBigDecimal(response.get("EPS")));
            overview.setRevenuePerShareTTM(parseBigDecimal(response.get("RevenuePerShareTTM")));
            overview.setProfitMargin(parseBigDecimal(response.get("ProfitMargin")));
            overview.setOperatingMarginTTM(parseBigDecimal(response.get("OperatingMarginTTM")));
            overview.setReturnOnAssetsTTM(parseBigDecimal(response.get("ReturnOnAssetsTTM")));
            overview.setReturnOnEquityTTM(parseBigDecimal(response.get("ReturnOnEquityTTM")));
            overview.setRevenueTTM(parseBigDecimal(response.get("RevenueTTM")));
            overview.setGrossProfitTTM(parseBigDecimal(response.get("GrossProfitTTM")));
            overview.setEbitda(parseBigDecimal(response.get("EBITDA")));

            // Métriques de trading
            overview.setFiftyTwoWeekHigh(parseBigDecimal(response.get("52WeekHigh")));
            overview.setFiftyTwoWeekLow(parseBigDecimal(response.get("52WeekLow")));
            overview.setFiftyDayMovingAverage(parseBigDecimal(response.get("50DayMovingAverage")));
            overview.setTwoHundredDayMovingAverage(parseBigDecimal(response.get("200DayMovingAverage")));
            overview.setSharesOutstanding(parseLong(response.get("SharesOutstanding")));
            overview.setExDividendDate((String) response.get("ExDividendDate"));

            return overview;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des données: " + e.getMessage());
        }
    }

    /**
     * Récupère l'indicateur RSI.
     * 
     * @param symbol Symbole boursier
     * @param interval Intervalle
     * @param timePeriod Période (défaut: 14)
     * @return Indicateurs techniques
     */
    public TechnicalIndicators getRSI(String symbol, String interval, int timePeriod) {
        try {
            validateSymbol(symbol);

            String url = String.format("%s?function=RSI&symbol=%s&interval=%s&time_period=%d&series_type=close&apikey=%s",
                    baseUrl, symbol.toUpperCase(), interval, timePeriod, apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Aucune donnée reçue");
            }

            Map<String, String> metadata = (Map<String, String>) response.get("Meta Data");
            Map<String, Map<String, String>> technicalAnalysis = 
                (Map<String, Map<String, String>>) response.get("Technical Analysis: RSI");

            if (technicalAnalysis == null || technicalAnalysis.isEmpty()) {
                throw new RuntimeException("Aucune donnée RSI trouvée");
            }

            // Récupérer la dernière valeur
            String lastDate = technicalAnalysis.keySet().iterator().next();
            Map<String, String> lastValue = technicalAnalysis.get(lastDate);

            TechnicalIndicators indicators = new TechnicalIndicators();
            indicators.setSymbol(symbol.toUpperCase());
            indicators.setIndicator("RSI");
            indicators.setInterval(interval);
            indicators.setLastRefreshed(metadata.get("3: Last Refreshed"));
            indicators.setRsi(new BigDecimal(lastValue.get("RSI")));

            return indicators;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du RSI: " + e.getMessage());
        }
    }

    /**
     * Récupère l'indicateur MACD.
     * 
     * @param symbol Symbole boursier
     * @param interval Intervalle
     * @return Indicateurs techniques
     */
    public TechnicalIndicators getMACD(String symbol, String interval) {
        try {
            validateSymbol(symbol);

            String url = String.format("%s?function=MACD&symbol=%s&interval=%s&series_type=close&apikey=%s",
                    baseUrl, symbol.toUpperCase(), interval, apiKey);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Aucune donnée reçue");
            }

            Map<String, String> metadata = (Map<String, String>) response.get("Meta Data");
            Map<String, Map<String, String>> technicalAnalysis = 
                (Map<String, Map<String, String>>) response.get("Technical Analysis: MACD");

            if (technicalAnalysis == null || technicalAnalysis.isEmpty()) {
                throw new RuntimeException("Aucune donnée MACD trouvée");
            }

            String lastDate = technicalAnalysis.keySet().iterator().next();
            Map<String, String> lastValue = technicalAnalysis.get(lastDate);

            TechnicalIndicators indicators = new TechnicalIndicators();
            indicators.setSymbol(symbol.toUpperCase());
            indicators.setIndicator("MACD");
            indicators.setInterval(interval);
            indicators.setLastRefreshed(metadata.get("3: Last Refreshed"));
            indicators.setMacd(new BigDecimal(lastValue.get("MACD")));
            indicators.setMacdSignal(new BigDecimal(lastValue.get("MACD_Signal")));
            indicators.setMacdHist(new BigDecimal(lastValue.get("MACD_Hist")));

            return indicators;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du MACD: " + e.getMessage());
        }
    }

    /**
     * Parse un BigDecimal depuis un Object.
     */
    private BigDecimal parseBigDecimal(Object value) {
        if (value == null || value.toString().equals("None") || value.toString().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse un Long depuis un Object.
     */
    private Long parseLong(Object value) {
        if (value == null || value.toString().equals("None") || value.toString().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Génère des données de démonstration pour les prix quotidiens.
     */
    private StockDataResponse generateDemoDailyData(String symbol) {
        StockDataResponse response = new StockDataResponse();
        response.setSymbol(symbol.toUpperCase());
        response.setInterval("daily");
        response.setLastRefreshed(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setIsDemo(true);
        
        // Prix de base selon le symbole
        Map<String, Double> basePrices = Map.of(
            "AAPL", 180.0,
            "TSLA", 240.0,
            "MSFT", 380.0,
            "GOOGL", 140.0,
            "AMZN", 150.0,
            "META", 320.0,
            "NVDA", 480.0,
            "AMD", 120.0
        );
        
        double basePrice = basePrices.getOrDefault(symbol.toUpperCase(), 100.0);
        List<StockDataPoint> dataPoints = new ArrayList<>();
        
        // Générer 30 jours de données
        for (int i = 29; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            
            // Variation aléatoire basée sur le jour
            double variation = Math.sin(i * 0.3) * 5 + (i % 3 - 1) * 2;
            double open = basePrice + variation;
            double close = open + (Math.sin(i * 0.5) * 3);
            double high = Math.max(open, close) + Math.abs(Math.sin(i * 0.7)) * 2;
            double low = Math.min(open, close) - Math.abs(Math.cos(i * 0.7)) * 2;
            long volume = (long) (10000000 + Math.abs(Math.sin(i * 0.4)) * 5000000);
            
            StockDataPoint point = new StockDataPoint();
            point.setTimestamp(date);
            point.setOpen(BigDecimal.valueOf(open));
            point.setHigh(BigDecimal.valueOf(high));
            point.setLow(BigDecimal.valueOf(low));
            point.setClose(BigDecimal.valueOf(close));
            point.setVolume(volume);
            
            dataPoints.add(point);
        }
        
        response.setTimeSeries(dataPoints);
        return response;
    }
}
