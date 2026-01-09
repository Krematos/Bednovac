package com.example.bednovac.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.bednovac.model.Case;
import com.example.bednovac.model.Currency;
import com.example.bednovac.model.Exchange;
import com.example.bednovac.util.Constants; // Pro API klíče, URL apod.

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture; // Pro asynchronní operace
import java.util.logging.Level; // Pro logování
import java.util.logging.Logger; // Pro logování

public class ApiClient {

    private static final Logger LOGGER = Logger.getLogger(ApiClient.class.getName()); // Pro logování chyb a informací
    private final ObjectMapper objectMapper; // Pro parsování JSON odpovědí
    private final HttpClient httpClient;

    // Konstruktor třídy ApiClient.
    public ApiClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Asynchronně získá aktuální cenu bedny z externího API.
     * Tato metoda předpokládá API, které vrací JSON s cenou v USD.
     *
     * @param caseName Název bedny (např. "Kilowatt Case"). Musí přesně odpovídat
     *                 formátu API.
     * @return CompletableFuture, který obsahuje instanci Case s aktualizovanou
     *         cenou, nebo null v případě chyby.
     */

    public CompletableFuture<Case> fetchCasePrice(String caseName) {
        String encodedCaseName;
        try {
            encodedCaseName = URLEncoder.encode(caseName, StandardCharsets.UTF_8.toString());
            String url = String.format(
                    "https://steamcommunity.com/market/priceoverview/?appid=%s&currency=%s&market_hash_name=%s", "730",
                    "6",
                    encodedCaseName); // URL pro získání ceny bedny z API (appid=730 je CS:GO, currency=6 je
                                      // pravděpodobně pro PLN nebo jinou měnu, ale Steam API to může brát různě,
                                      // pozor na to)

            URI uri = URI.create(url);
            HttpRequest reguest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            return httpClient.sendAsync(reguest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(response -> {
                        try {
                            JsonNode rootNode = objectMapper.readTree(response);

                            String lowestPriceStr = rootNode.path("lowest_price").asText(null);
                            if (lowestPriceStr == null || lowestPriceStr.isEmpty()) {
                                LOGGER.log(Level.WARNING,
                                        "Lowest price not found for case: " + caseName + ", response: " + response);
                                return null; // Pokud lowest_price není v odpovědi, vrátí null
                            }
                            // Odstranění měny a převod na číslo
                            String normalized = lowestPriceStr.replaceAll("[^0-9,\\.]", "").replace(",", "."); // Nahrazení
                                                                                                               // čárky
                                                                                                               // tečkou
                                                                                                               // pro
                                                                                                               // desetinnou
                                                                                                               // část
                            double price = Double.parseDouble(normalized);

                            LOGGER.info("Successfully fetched price for case: " + caseName + ", price: " + price);

                            return new Case(caseName, price); // Vrátí objekt Case s cenou

                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, "Failed to parse response for case: " + caseName, e);
                            return null; // Pokud dojde k chybě při parsování, vrátí null
                        }
                    })
                    .exceptionally(e -> {
                        LOGGER.log(Level.WARNING, "Failed to fetch case price for: " + caseName, e);
                        return null; // Pokud dojde k chybě při volání API, vrátí null
                    });
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to encode case name: " + caseName, e);
            return CompletableFuture.completedFuture(null); // Pokud dojde k chybě při kódování, vrátí null
        }

    }

    /**
     * Asynchronně získá aktuální směnné kurzy z externího API.
     * Tato metoda předpokládá API jako ExchangeRate-API.com s USD jako základní
     * měnou.
     *
     * @return CompletableFuture, který obsahuje instanci ExchangeRate s aktuálními
     *         kurzy, nebo null v případě chyby.
     */

    public CompletableFuture<Exchange> fetchExchangeRates() {
        String url = "https://api.frankfurter.app/latest?base=USD"; // URL pro získání směnných kurzů s USD jako
                                                                    // základní měnou

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        LOGGER.log(Level.INFO, "Fetching exchange rates from API: " + url);

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(responseBody -> {
                    LOGGER.info("Exchange rates API response: " + responseBody);
                    try {
                        JsonNode rootNode = objectMapper.readTree(responseBody);
                        if (rootNode.has("error-type")) {
                            LOGGER.log(Level.WARNING, "API returned error: " + rootNode.path("error-type").asText());
                            return null; // Pokud API vrátí chybu, vrátí null
                        }
                        Currency baseCurrency = Currency.fromString(rootNode.path("base").asText("USD"));
                        Map<Currency, Double> rates = new HashMap<>();
                        JsonNode ratesNode = rootNode.path("rates");
                        for (Currency currency : Currency.values()) {
                            if (ratesNode.has(currency.name())) {
                                rates.put(currency, ratesNode.path(currency.name()).asDouble(0.0));
                            }
                        }
                        Exchange exchange = new Exchange(baseCurrency, rates);
                        LOGGER.info("Successfully fetched exchange rates: " + exchange);
                        return exchange; // Vrátí objekt Exchange s aktuálními kurzy
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Failed to parse exchange rates response", e);
                        return null; // Pokud dojde k chybě při parsování, vrátí null
                    }
                })
                .exceptionally(e -> {
                    LOGGER.log(Level.WARNING, "Failed to fetch exchange rates", e);
                    return null; // Pokud dojde k chybě při volání API, vrátí null
                });
    }
}
