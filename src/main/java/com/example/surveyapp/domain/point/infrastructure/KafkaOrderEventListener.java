//package com.example.surveyapp.domain.point.infrastructure;
//
//import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
//import com.example.surveyapp.domain.point.application.eventhandler.OrderPointEventHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class KafkaOrderEventListener{
//    private final OrderPointEventHandler orderPointEventHandler;
//
//    public KafkaOrderEventListener(OrderPointEventHandler orderPointEventHandler) {
//        this.orderPointEventHandler = orderPointEventHandler;
//    }
//
//    @KafkaListener(topics = "order-created", groupId = "group_1", containerFactory = "kafkaListenerContainerFactory")
//    public void handle(OrderCreateEvent event){
//        log.info("받앗어유");
//        orderPointEventHandler.handleOrderCreateEvent(event);
//    }
//}
