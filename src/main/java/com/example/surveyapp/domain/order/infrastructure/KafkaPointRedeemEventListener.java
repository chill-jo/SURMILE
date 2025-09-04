package com.example.surveyapp.domain.order.infrastructure;

import com.example.surveyapp.domain.order.application.eventhandler.OrderCreatedEventHandler;
import com.example.surveyapp.domain.point.domain.event.PointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.event.PointRedeemSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPointRedeemEventListener {
    private final OrderCreatedEventHandler eventHandler;

    @KafkaListener(topics = "order-redeem-succeeded", groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleSucceedEvent(PointRedeemSucceededEvent event){
        eventHandler.pointSucceedEvent(event);
    }
    
    @KafkaListener(topics = "order-redeem-failed", groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleFailedEvent(PointRedeemFailedEvent event){
        eventHandler.pointFailedEvent(event);
    }
}
