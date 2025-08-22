package com.example.surveyapp.domain.order.domain.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING_PAYMENT("결제준비"),
    CONFIRMED("결제완료"),
    CANCEL("결제취소");

    private final String orderStatus;
}
