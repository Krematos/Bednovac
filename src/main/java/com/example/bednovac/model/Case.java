package com.example.bednovac.model;

import java.util.Objects;

public class Case {
    private String caseName;
    private double casePriceUsd;

    /**
     * Constructor for Case class.
     *
     * @param caseName Name of the case.
     * @param casePriceUsd Price of the case.
     */
    public Case( String caseName, double casePriceUsd) {
        if(caseName == null || caseName.isEmpty()) {
            throw new IllegalArgumentException("Case name cannot be null or empty");
        }
        if(casePriceUsd < 0) {
            throw new IllegalArgumentException("Case price cannot be negative");
        }
        this.caseName = caseName;
        this.casePriceUsd = casePriceUsd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

    public String getName() {
        return caseName;
    }

    public void setName(String name) {
        this.caseName = caseName;
    }

    public double getPrice() {
        return casePriceUsd;
    }

    public void setPrice(double price) {
        this.casePriceUsd = price;
    }



}
