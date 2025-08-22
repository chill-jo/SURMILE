package com.example.surveyapp.domain.payment.domain.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointStatus {
    CONFIRM("완료"),
    PENDING("결제 진행 중"),
    FAILED("실패");

    private final String status;
}
