package com.example.surveyapp.domain.order.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderCreateEvent {
    private final Long orderId;

    private final Long userId;

    private final Long totalAmount;

}
