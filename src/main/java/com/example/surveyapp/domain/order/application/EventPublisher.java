package com.example.surveyapp.domain.order.application;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;

public interface EventPublisher {
    void publishEvent(OrderCreateEvent event);
}
