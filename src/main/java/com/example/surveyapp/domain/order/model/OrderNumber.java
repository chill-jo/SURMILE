package com.example.surveyapp.domain.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderNumber {

    @Column(name = "order_number", nullable = false,unique = true, length = 36)
    private String value;

    public OrderNumber(String value) {
        this.value = value;
    }

    //UUID 날짜 + 랜덤 숫자 8개를 만들기 위한 generator
    public static OrderNumber generator() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randoNum = (int) ((Math.random() * 90000000) + 10000000); //8자리 랜덤 숫자 만들기
        return new OrderNumber(date+randoNum);
    }
}
