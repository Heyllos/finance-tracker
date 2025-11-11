package com.fintrack.service;

import com.fintrack.dto.CryptoPriceResponse;
import com.fintrack.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service pour récupérer les données des API externes (Forex et Crypto).
 * Les résultats sont mis en cache pendant 1 heure.
 */
@Service
public class ExternalApiService {

    @Autowired
    private WebClient webClient;

    // API gratuite pour les taux de change : https://exchangerate-api.com
    private static final String EXCHANGE_API_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    // API gratuite pour les crypto : https://api.coingecko.com
    private static final String CRYPTO_API_URL = "https://api.coingecko.com/api/v3/simple/price";

    /**
     * Récupère les taux de change pour une devise de base.
     * Résultats mis en cache pendant 1h (3600 secondes).
     * 
     * @param base Devise de base (ex: USD, EUR)
     * @return Taux de change
     */
    @Cacheable(value = "exchangeRates", key = "#base")
    public ExchangeRateResponse getExchangeRates(String base) {
        try {
            // Appel à l'API externe
            Map<String, Object> response = webClient.get()
                    .uri(EXCHANGE_API_URL + base.toUpperCase())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("rates")) {
                return createDefaultExchangeRates(base);
            }

            // Extraction des taux
            Map<String, Object> ratesMap = (Map<String, Object>) response.get("rates");
            Map<String, BigDecimal> rates = new HashMap<>();
            
            for (Map.Entry<String, Object> entry : ratesMap.entrySet()) {
                BigDecimal rate = new BigDecimal(entry.getValue().toString())
                        .setScale(4, RoundingMode.HALF_UP);
                rates.put(entry.getKey(), rate);
            }

            return new ExchangeRateResponse(base.toUpperCase(), LocalDateTime.now(), rates);
            
        } catch (Exception e) {
            // En cas d'erreur, retourner des données par défaut
            return createDefaultExchangeRates(base);
        }
    }

    /**
     * Récupère les taux de change entre deux devises spécifiques.
     * 
     * @param base Devise de base
     * @param target Devise cible
     * @return Taux de change
     */
    public ExchangeRateResponse getExchangeRates(String base, String target) {
        ExchangeRateResponse allRates = getExchangeRates(base);
        
        if (allRates.getRates().containsKey(target.toUpperCase())) {
            Map<String, BigDecimal> filteredRates = new HashMap<>();
            filteredRates.put(target.toUpperCase(), allRates.getRates().get(target.toUpperCase()));
            return new ExchangeRateResponse(base.toUpperCase(), allRates.getTimestamp(), filteredRates);
        }
        
        return allRates;
    }

    /**
     * Récupère les prix des principales crypto-monnaies.
     * Résultats mis en cache pendant 1h.
     * 
     * @return Liste des prix des cryptos
     */
    @Cacheable(value = "cryptoPrices")
    public List<CryptoPriceResponse> getCryptoPrices() {
        try {
            // Liste des cryptos à récupérer
            String cryptoIds = "bitcoin,ethereum,binancecoin,cardano,solana,ripple,polkadot,dogecoin,avalanche-2,chainlink";
            
            // Appel à l'API CoinGecko
            Map<String, Map<String, Object>> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.coingecko.com")
                            .path("/api/v3/simple/price")
                            .queryParam("ids", cryptoIds)
                            .queryParam("vs_currencies", "usd,eur")
                            .queryParam("include_market_cap", "true")
                            .queryParam("include_24hr_vol", "true")
                            .queryParam("include_24hr_change", "true")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                return createDefaultCryptoPrices();
            }

            List<CryptoPriceResponse> cryptoPrices = new ArrayList<>();
            
            for (Map.Entry<String, Map<String, Object>> entry : response.entrySet()) {
                String id = entry.getKey();
                Map<String, Object> data = entry.getValue();
                
                CryptoPriceResponse crypto = new CryptoPriceResponse();
                crypto.setSymbol(getSymbolFromId(id));
                crypto.setName(getNameFromId(id));
                crypto.setPriceUsd(new BigDecimal(data.getOrDefault("usd", 0).toString()).setScale(2, RoundingMode.HALF_UP));
                crypto.setPriceEur(new BigDecimal(data.getOrDefault("eur", 0).toString()).setScale(2, RoundingMode.HALF_UP));
                
                if (data.containsKey("usd_24h_change")) {
                    crypto.setChangePercent24h(new BigDecimal(data.get("usd_24h_change").toString()).setScale(2, RoundingMode.HALF_UP));
                }
                if (data.containsKey("usd_market_cap")) {
                    crypto.setMarketCapUsd(new BigDecimal(data.get("usd_market_cap").toString()).setScale(0, RoundingMode.HALF_UP));
                }
                if (data.containsKey("usd_24h_vol")) {
                    crypto.setVolume24h(new BigDecimal(data.get("usd_24h_vol").toString()).setScale(0, RoundingMode.HALF_UP));
                }
                
                crypto.setTimestamp(LocalDateTime.now());
                cryptoPrices.add(crypto);
            }
            
            return cryptoPrices;
            
        } catch (Exception e) {
            // En cas d'erreur, retourner des données par défaut
            return createDefaultCryptoPrices();
        }
    }

    /**
     * Crée des taux de change par défaut en cas d'erreur API.
     */
    private ExchangeRateResponse createDefaultExchangeRates(String base) {
        Map<String, BigDecimal> defaultRates = new HashMap<>();
        defaultRates.put("USD", new BigDecimal("1.0000"));
        defaultRates.put("EUR", new BigDecimal("0.9200"));
        defaultRates.put("GBP", new BigDecimal("0.7900"));
        defaultRates.put("JPY", new BigDecimal("149.5000"));
        defaultRates.put("CHF", new BigDecimal("0.8800"));
        defaultRates.put("CAD", new BigDecimal("1.3600"));
        defaultRates.put("AUD", new BigDecimal("1.5300"));
        
        return new ExchangeRateResponse(base.toUpperCase(), LocalDateTime.now(), defaultRates);
    }

    /**
     * Crée des prix de crypto par défaut en cas d'erreur API.
     */
    private List<CryptoPriceResponse> createDefaultCryptoPrices() {
        List<CryptoPriceResponse> defaultPrices = new ArrayList<>();
        
        defaultPrices.add(createCrypto("BTC", "Bitcoin", "43250.00", "39800.00", "2.5"));
        defaultPrices.add(createCrypto("ETH", "Ethereum", "2280.00", "2100.00", "3.2"));
        defaultPrices.add(createCrypto("BNB", "Binance Coin", "315.00", "290.00", "1.8"));
        defaultPrices.add(createCrypto("ADA", "Cardano", "0.52", "0.48", "-0.5"));
        defaultPrices.add(createCrypto("SOL", "Solana", "98.50", "90.70", "4.1"));
        defaultPrices.add(createCrypto("XRP", "Ripple", "0.62", "0.57", "1.2"));
        defaultPrices.add(createCrypto("DOT", "Polkadot", "7.25", "6.68", "-1.3"));
        defaultPrices.add(createCrypto("DOGE", "Dogecoin", "0.085", "0.078", "0.8"));
        
        return defaultPrices;
    }

    private CryptoPriceResponse createCrypto(String symbol, String name, String priceUsd, String priceEur, String change) {
        CryptoPriceResponse crypto = new CryptoPriceResponse();
        crypto.setSymbol(symbol);
        crypto.setName(name);
        crypto.setPriceUsd(new BigDecimal(priceUsd));
        crypto.setPriceEur(new BigDecimal(priceEur));
        crypto.setChangePercent24h(new BigDecimal(change));
        crypto.setTimestamp(LocalDateTime.now());
        return crypto;
    }

    private String getSymbolFromId(String id) {
        Map<String, String> symbols = new HashMap<>();
        symbols.put("bitcoin", "BTC");
        symbols.put("ethereum", "ETH");
        symbols.put("binancecoin", "BNB");
        symbols.put("cardano", "ADA");
        symbols.put("solana", "SOL");
        symbols.put("ripple", "XRP");
        symbols.put("polkadot", "DOT");
        symbols.put("dogecoin", "DOGE");
        symbols.put("avalanche-2", "AVAX");
        symbols.put("chainlink", "LINK");
        return symbols.getOrDefault(id, id.toUpperCase());
    }

    private String getNameFromId(String id) {
        Map<String, String> names = new HashMap<>();
        names.put("bitcoin", "Bitcoin");
        names.put("ethereum", "Ethereum");
        names.put("binancecoin", "Binance Coin");
        names.put("cardano", "Cardano");
        names.put("solana", "Solana");
        names.put("ripple", "Ripple");
        names.put("polkadot", "Polkadot");
        names.put("dogecoin", "Dogecoin");
        names.put("avalanche-2", "Avalanche");
        names.put("chainlink", "Chainlink");
        return names.getOrDefault(id, id.substring(0, 1).toUpperCase() + id.substring(1));
    }
}
