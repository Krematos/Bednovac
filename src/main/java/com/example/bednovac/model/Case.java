package com.example.bednovac.model;

import java.util.Objects;

/**
 * Třída reprezentující bednu (Case) ve hře.
 * Obsahuje název bedny a její aktuální cenu v USD.
 */
public class Case {
    private String caseName;
    private double casePriceUsd;

    /**
     * Konstruktor třídy Case.
     *
     * @param caseName     Název bedny.
     * @param casePriceUsd Cena bedny v USD.
     */
    public Case(String caseName, double casePriceUsd) {
        if (caseName == null || caseName.isEmpty()) {
            throw new IllegalArgumentException("Case name cannot be null or empty");
        }
        if (casePriceUsd < 0) {
            throw new IllegalArgumentException("Case price cannot be negative");
        }
        this.caseName = caseName;
        this.casePriceUsd = casePriceUsd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Case aCase = (Case) o;
        return Double.compare(aCase.casePriceUsd, casePriceUsd) == 0 &&
                Objects.equals(caseName, aCase.caseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseName, casePriceUsd);
    }

    @Override
    public String toString() {
        return "Case{" +
                "caseName='" + caseName + '\'' +
                ", casePriceUsd=" + casePriceUsd +
                '}';
    }

    // Getters and Setters

    /**
     * Vrátí název bedny.
     * 
     * @return Název bedny.
     */
    public String getName() {
        return caseName;
    }

    /**
     * Nastaví název bedny.
     * 
     * @param name Nový název bedny.
     */
    public void setName(String name) {
        this.caseName = caseName;
    }

    /**
     * Vrátí cenu bedny v USD.
     * 
     * @return Cena bedny.
     */
    public double getPrice() {
        return casePriceUsd;
    }

    /**
     * Nastaví cenu bedny v USD.
     * 
     * @param price Nová cena bedny.
     */
    public void setPrice(double price) {
        this.casePriceUsd = price;
    }

}
