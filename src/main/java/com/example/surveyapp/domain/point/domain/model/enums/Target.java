package com.example.surveyapp.domain.point.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Target {
    ORDER("주문"),
    SURVEY("설문"),
    PAYMENTS("결제");

    private final String target;



}
