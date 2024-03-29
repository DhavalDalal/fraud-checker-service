package com.tsys.fraud_checker.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Currency;

public class Money {
    //  The @Parameter annotation is for the parameters of an API resource request,
    //  whereas @Schema is for properties of the model.
    @Schema(
            description = "Currency of the transaction",
            name = "currency",
            implementation = String.class,
            required = true,
            example = "INR")
    @NotNull(message = "is required!")
    public final Currency currency;

    @Schema(
            description = "Amount of the transaction",
            name = "amount",
            implementation = Double.class,
            required = true,
            example = "1234.56")
    @NotNull(message = "is required!")
    public final Double amount;

    @Deprecated
    public Money() {
        this(null, null);
    }

    public Money(Currency currency, Double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Money add(Money other) {
        if (currency != other.currency)
            throw new IllegalArgumentException("For addition the currencies must be same!");

        return new Money(currency, amount + other.amount);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", currency.getSymbol(), amount);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other.getClass() != Money.class)
            return false;

        if (this == other)
            return true;

        Money that = (Money) other;
        return currency.equals(that.currency)
                && amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + currency.hashCode();
        hash = 97 * hash + amount.hashCode();
        return hash;
    }
}