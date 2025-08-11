package com.example.surveyapp.domain.order.event;

import com.example.surveyapp.global.config.event.Event;
import lombok.Getter;

@Getter
public class OrderCreateEvent extends Event {
    private final Long orderId;

    private final Long userId;

    private final Long totalAmount;

    public OrderCreateEvent(Long orderId, Long userId, Long totalAmount) {
        super();
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}
