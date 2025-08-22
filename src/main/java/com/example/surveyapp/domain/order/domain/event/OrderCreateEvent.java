package com.example.surveyapp.domain.order.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateEvent{
    private Long orderId;
    private Long userId;
    private Long totalAmount;
}
