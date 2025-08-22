package com.example.surveyapp.domain.point.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PointChargeFailedEvent {
    private final Long userId;
    private final Long paymentId;
}
