package com.example.surveyapp.domain.order.event;

import com.example.surveyapp.domain.order.model.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class OrderCreateEvent  {
    private final Order order;

    private final Long userId;
}
