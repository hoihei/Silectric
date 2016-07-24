package net.alaindonesia.silectric.model;

import android.support.annotation.NonNull;

import java.util.Currency;
import java.util.Locale;

public class SilentricCurrency implements Comparable {

    private String countryName;
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


    public SilentricCurrency(String countryName, Currency currency){
        this.countryName = countryName;
        this.currency = currency;
    }

    public SilentricCurrency(Currency currency){
        this.currency = currency;
    }

    public String toString(){
        return this.countryName + " (" + this.currency.getSymbol() + ")";
    }

    @Override
    public int compareTo(@NonNull Object another) {
        SilentricCurrency that = (SilentricCurrency) another;
        return this.countryName.compareTo(that.countryName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SilentricCurrency that = (SilentricCurrency) o;

        return currency != null ? currency.equals(that.currency) : that.currency == null;

    }

    @Override
    public int hashCode() {
        return currency != null ? currency.hashCode() : 0;
    }
}