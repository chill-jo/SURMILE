package com.example.surveyapp.domain.payment.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PointChargeEvent {

    private final Long userId;
    private final Long paymentId;
    private final Long amount;
}
