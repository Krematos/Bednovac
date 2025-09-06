package com.example.bednovac.service;

import com.example.bednovac.model.Case;
import com.example.bednovac.model.Exchange;
import com.example.bednovac.model.Currency;
import com.example.bednovac.util.Constants;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriceService {
    private static final Logger LOGGER = Logger.getLogger(PriceService.class.getName());

    private final ApiClient apiClient;

    private final Map<String, CacheEntry<Case>> casePriceCache = Collections.synchronizedMap(new HashMap<>());

    private CacheEntry<Exchange> exchangeCache;

    public PriceService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<Case> getCasePrice(String caseName) {
        CacheEntry<Case> cachedEntry = casePriceCache.get(caseName);
        if (cachedEntry != null && !cachedEntry.data.getName().isEmpty() && !isCacheExpired(cachedEntry.timestamp, Constants.CASE_CACHE_EXPIRY_MINUTES)) {
            LOGGER.log(Level.INFO, "Returning cached price for case: " + caseName);
            return CompletableFuture.completedFuture(cachedEntry.data);
        }

        LOGGER.info("Fetching case price from API for case: " + caseName);
        return apiClient.fetchCasePrice(caseName)
                .thenApply(casePrice -> {
                    if (casePrice != null) {
                        casePriceCache.put(caseName, new CacheEntry<>(casePrice, LocalDateTime.now().plusMinutes(10)));
                        LOGGER.log(Level.INFO, "Cached price for case: " + caseName);
                    } else {
                        LOGGER.log(Level.WARNING, "Failed to fetch case price for: " + caseName);
                    }
                    return casePrice;
                })
        .exceptionally(e -> {
            LOGGER.log(Level.WARNING, "FAILED to fetch case price for: " + caseName, e);
            if(cachedEntry != null) {
                LOGGER.log(Level.INFO, "Returning cached price for case: " + caseName);
                return cachedEntry.data;
            }
            return null;
        });
    }

    public CompletableFuture<Exchange> getExchange() {
        if (exchangeCache != null && !isCacheExpired(exchangeCache.timestamp, Constants.EXCHANGE_RATE_CACHE_EXPIRY_MINUTES)) {
            LOGGER.log(Level.INFO, "Returning exchange rates from cache.");
            return CompletableFuture.completedFuture(exchangeCache.data);
        }
        LOGGER.info("Fetching exchange rates from API.");
        return apiClient.fetchExchangeRates()
                .thenApply(exchange -> {
                    if (exchange != null) {
                        exchangeCache = new CacheEntry<>(exchange, LocalDateTime.now());
                        LOGGER.log(Level.INFO, "Cached exchange rates.");
                    }
                    return exchange;
                })
                .exceptionally(e -> {
                    LOGGER.log(Level.WARNING, "FAILED to fetch exchange rates.", e);
                    if (exchangeCache != null && !isCacheExpired(exchangeCache.timestamp, Constants.EXCHANGE_RATE_CACHE_EXPIRY_MINUTES)) {
                        LOGGER.log(Level.INFO, "Returning cached exchange rates.");
                        return exchangeCache.data;
                    }
                    return null;
                });


    }

    /**
     * Pomocná metoda pro kontrolu expirace cache.
     * @param timestamp Čas, kdy byla data uložena do cache.
     * @param expiryMinutes Počet minut, po kterých cache expiruje.
     * @return True, pokud cache expirovala, jinak false.
     */
    private boolean isCacheExpired(LocalDateTime timestamp, int expiryMinutes) {
        return timestamp.plusMinutes(expiryMinutes).isBefore(LocalDateTime.now());
    }

    /**
     * Vnitřní pomocná třída pro uchovávání dat v cache spolu s časovým razítkem.
     * @param <T> Typ dat uložených v cache.
     */
    private static class CacheEntry<T> {
        private final T data;
        private final LocalDateTime timestamp;

        CacheEntry(T data, LocalDateTime timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }



    public double keyPrice() {
        return Constants.KEY_PRICE_USD;
    }



    public double getKeyPrice() {
        return keyPrice();
    }













}
