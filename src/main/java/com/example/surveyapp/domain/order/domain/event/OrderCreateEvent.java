package com.example.surveyapp.domain.order.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateEvent{
    private Long orderId;
    private Long userId;
    private Long totalAmount;
}
