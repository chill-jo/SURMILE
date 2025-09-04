package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;

public interface PaymentEventPublisher {
    void publishEvent(PointChargeEvent event);
}
