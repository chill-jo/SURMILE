package com.example.surveyapp.domain.payment.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PointChargeEvent {

    private Long userId;
    private Long paymentId;
    private Long amount;
}
