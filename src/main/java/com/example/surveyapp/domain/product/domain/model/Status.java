package com.example.surveyapp.domain.product.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ON_SALE("판매중"),
    STOPPED_SALE("판매 중단");

    private final String status;
}
