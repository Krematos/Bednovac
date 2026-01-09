package com.example.bednovac.util;

import com.example.bednovac.model.Currency;

/**
 * Třída obsahující konstanty používané v aplikaci.
 * Zahrnuje ceny, API klíče, URL a nastavení cache.
 */

public class Constants {

    public static final double KEY_PRICE_USD = 2.49;

    public static final Currency BASE_CURRENCY = Currency.USD;

    public static final String CASE_PRICE_API_URL = "https://steamcommunity.com/market/priceoverview/?appid=%s&currency=%s&market_hash_name=%s"; // URL pro API získání ceny case

    public static final String EXCHANGE_RATE_API_URL = "https://api.frankfurter.app/latest?base=USD" ; // URL pro API získání směnných kurzů

    public static final int EXCHANGE_RATE_CACHE_EXPIRY_MINUTES = 120; // 2 hodiny - Doba expirace cache směnných kurzů v minutách

    public static final int CASE_CACHE_EXPIRY_MINUTES = 60; // 1 hodina - Doba expirace cache cen case v minutách

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
