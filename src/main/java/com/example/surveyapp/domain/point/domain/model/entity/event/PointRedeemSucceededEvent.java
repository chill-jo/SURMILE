package com.example.surveyapp.domain.point.domain.model.entity.event;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointRedeemSucceededEvent {
    private final Long orderId;

    private final Long userId;
}
