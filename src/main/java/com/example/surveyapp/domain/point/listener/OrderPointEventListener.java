package com.example.surveyapp.domain.point.listener;

import com.example.surveyapp.domain.order.event.OrderCreateEvent;
import com.example.surveyapp.domain.order.facade.PointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPointEventListener {

    private final PointFacade pointFacade;

    @Async
    @EventListener
    public void handleOrderCreateEvent(OrderCreateEvent event) {
        pointFacade.redeem(event.getUserId(),
                event.getOrder().onlyOneOrderItem().getPrice());
    }
}
