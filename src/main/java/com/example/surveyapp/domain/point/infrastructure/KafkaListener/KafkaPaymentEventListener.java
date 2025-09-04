package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.point.application.eventhandler.PaymentPointEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentEventListener {

    private final PaymentPointEventHandler paymentPointEventHandler;

    @KafkaListener(topics = "payment-charge-created", groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(PointChargeEvent event){
        paymentPointEventHandler.handlePointChargeEvent(event);
    }

}
