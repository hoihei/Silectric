package net.alaindonesia.silectric.model;

import android.support.annotation.NonNull;

import java.util.Currency;

public class SilectricCurrency implements Comparable {

    private String countryName;
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


    public SilectricCurrency(String countryName, Currency currency){
        this.countryName = countryName;
        this.currency = currency;
    }

    public SilectricCurrency(Currency currency){
        this.currency = currency;
    }

    public String toString(){
        return this.countryName + " (" + this.currency.getSymbol() + ")";
    }

    @Override
    public int compareTo(@NonNull Object another) {
        SilectricCurrency that = (SilectricCurrency) another;
        return this.countryName.compareTo(that.countryName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SilectricCurrency that = (SilectricCurrency) o;

        return currency != null ? currency.equals(that.currency) : that.currency == null;

    }

    @Override
    public int hashCode() {
        return currency != null ? currency.hashCode() : 0;
    }
}