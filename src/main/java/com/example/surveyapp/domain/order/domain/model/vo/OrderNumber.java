package com.example.surveyapp.domain.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderNumber {

    @Column(name = "order_number", nullable = false,unique = true, length = 20)
    private String value;

    public OrderNumber(String value) {
        this.value = value;
    }

    //UUID 날짜 + 시퀀스 조합 8개를 만들기 위한 generator
    public static OrderNumber generator() {
        String date = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-","");
        return new OrderNumber(date+uuid.substring(0,10));
    }
}
