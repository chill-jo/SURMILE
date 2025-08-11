package com.example.surveyapp.domain.point.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    @Column(name = "amount", nullable = false)
    Long value;

    Currency currency;

    public Money(Long value, Currency currency){
        this.value = value;
        this.currency = currency;
    }

    public static Money krw(Long value){
        return new Money(value, Currency.getInstance("KRW"));
    }
}
