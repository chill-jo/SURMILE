package com.example.surveyapp.domain.point.domain.model.entity.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointRedeemFailedEvent {
    private final Long orderId;

    private final Long userId;

}
