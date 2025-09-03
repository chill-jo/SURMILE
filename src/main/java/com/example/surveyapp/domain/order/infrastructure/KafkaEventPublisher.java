//package com.example.surveyapp.domain.order.infrastructure;
//
//import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
//import com.example.surveyapp.domain.order.application.EventPublisher;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaEventPublisher implements EventPublisher {
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @Override
//    public void publishEvent(OrderCreateEvent event) {
//        kafkaTemplate.send("order-created", event);
//    }
//}
