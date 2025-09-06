package com.example.bednovac.model;

public enum Currency {
    USD("US Dollar"),
    EUR("Euro"),
    CZK("Czech Koruna");

    private final String fullName;

    /**
     * Konstruktor pro enum Currency.
     * @param fullName  měny (např. Kč, €, $).
     */
    Currency(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Pomocná metoda pro získání instance Currency z řetězce (např. z ComboBoxu).
     * Může být užitečné, pokud potřebuješ převést uživatelský vstup na enum.
     * @param currencyCode Kód měny (např. "CZK", "EUR", "USD").
     * @return Instance Currency odpovídající kódu.
     * @throws IllegalArgumentException Pokud kód neodpovídá žádné definované měně.
     */
    public static Currency fromString(String currencyCode) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(currencyCode)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + currencyCode);
    }
}
