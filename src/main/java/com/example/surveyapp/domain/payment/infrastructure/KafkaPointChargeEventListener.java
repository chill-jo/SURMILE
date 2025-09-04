package com.example.surveyapp.domain.payment.infrastructure;

import com.example.surveyapp.domain.payment.application.eventhandler.PaymentEventHandler;
import com.example.surveyapp.domain.point.domain.event.PointChargeFailedEvent;
import com.example.surveyapp.domain.point.domain.event.PointChargeSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPointChargeEventListener {
    private final PaymentEventHandler eventHandler;

    @KafkaListener(topics = "point-charge-succeeded", groupId = "payment-group", containerFactory = "kafkaListenerContainerFactory" )
    public void handleSucceededEvent(PointChargeSucceededEvent event){
        eventHandler.handleChargeSuccessEvent(event);
    }

    @KafkaListener(topics = "point-charge-failed", groupId = "payment-group", containerFactory = "kafkaListenerContainerFactory" )
    public void handleFailedEvent(PointChargeFailedEvent event){
        eventHandler.handleChargeFailEvent(event);
    }
}
