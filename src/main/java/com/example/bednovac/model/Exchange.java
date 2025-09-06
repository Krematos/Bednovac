package com.example.bednovac.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;
public class Exchange {
    private final Currency baseCurrency;
    private final Map<Currency, Double> rates; // Kód cílové měny -> směnný kurz (kolik cílové měny za 1 jednotku baseCurrency)

    /**
     * Konstruktor pro vytvoření instance směnných kurzů.
     * @param baseCurrency Základní měna, ke které se kurzy vztahují.
     * @param rates Mapa směnných kurzů, kde klíčem je kód cílové měny a hodnotou je kurz.
     * Příklad: Pokud baseCurrency je USD, pak {"EUR": 0.92, "CZK": 23.5}.
     * To znamená 1 USD = 0.92 EUR, 1 USD = 23.5 CZK.
     */

    public Exchange(Currency baseCurrency, Map<Currency, Double> rates) {
        if (baseCurrency == null) {
            throw new IllegalArgumentException("Base currency cannot be null");
        }
        if (rates == null || rates.isEmpty()) {
            throw new IllegalArgumentException("Rates cannot be null or empty");
        }
        this.baseCurrency = baseCurrency;
        this.rates = new HashMap<>(rates); // Vytvoříme kopii mapy, aby byla neměnná
    }


    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * Získá směnný kurz z základní měny na zadanou cílovou měnu.
     * Příklad: Pokud baseCurrency je USD a targetCurrency je EUR, vrátí kurz 1 USD = X EUR.
     * @param targetCurrency Cílová měna.
     * @return Směnný kurz.
     * @throws IllegalArgumentException Pokud kurz pro danou cílovou měnu není dostupný.
     */
    public double getRate(Currency targetCurrency) {
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency cannot be null");
        }
        if (baseCurrency.equals(targetCurrency)) {
            return 1.0; // Kurz pro stejnou měnu je vždy 1
        }
        Double rate = rates.get(targetCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("Rate for " + targetCurrency + " not found");
        }
        return rate;
    }
    /**
     * Pomocná metoda pro převod částky z jedné měny na jinou s využitím těchto kurzů.
     * Tato metoda předpokládá, že 'this.baseCurrency' je referenční bod.
     *
     * @param amount Částka k převodu.
     * @param fromMena Měna, ze které se převádí.
     * @param toMena Měna, na kterou se převádí.
     * @return Převdená částka.
     * @throws IllegalArgumentException Pokud kurz pro dané měny není dostupný nebo je neplatný.
     */
    public double convert(double amount, Currency fromMena, Currency toMena) {
        if(amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (fromMena == null || toMena == null) {
            throw new IllegalArgumentException("Currencies cannot be null");
        }
        if (fromMena.equals(baseCurrency)) {
           return amount * getRate(toMena); // Převod z baseCurrency na toMena
        }
        else if(toMena.equals(baseCurrency)) {
            double rateFromBase = getRate(fromMena);
            return amount / rateFromBase; // Převod z fromMena na baseCurrency
        }
        else {
            double amountInBase = amount / getRate(fromMena); // Nejprve převedeme amount na baseCurrency
            return amountInBase * getRate(toMena); // Poté převedeme na toMena
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exchange)) return false;
        Exchange that = (Exchange) o;
        return baseCurrency == that.baseCurrency && Objects.equals(rates, that.rates);
    }
    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, rates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Exchange{");
        sb.append("baseCurrency=").append(baseCurrency);
        sb.append(", rates={");
        for (Map.Entry<Currency, Double> entry : rates.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        if (!rates.isEmpty()) {
            sb.setLength(sb.length() - 2); // Odstraní poslední čárku a mezeru
        }
        sb.append("}}");
        return sb.toString();
    }


}
