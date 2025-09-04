package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.point.application.eventhandler.OrderPointEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventListener{
    private final OrderPointEventHandler orderPointEventHandler;

    @KafkaListener(topics = "order-created", groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(OrderCreateEvent event){
        log.info("받앗어유");
        orderPointEventHandler.handleOrderCreateEvent(event);
    }
}
