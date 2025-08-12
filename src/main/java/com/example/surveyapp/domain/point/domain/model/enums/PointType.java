package com.example.surveyapp.domain.point.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    CHARGE("충전"),
    EARN("지급"),
    USAGE("사용");

    private final String status;
}
