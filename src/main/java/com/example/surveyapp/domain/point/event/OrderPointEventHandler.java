package com.example.surveyapp.domain.point.event;

import com.example.surveyapp.domain.order.event.OrderCreateEvent;
import com.example.surveyapp.domain.order.facade.PointFacade;
import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderPointEventHandler {

    private final PointFacade pointFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreateEvent(OrderCreateEvent event) {
        pointFacade.decreasePoint(event.getUserId(),
                PointPoints.create(event.getTotalAmount()),
                        event.getOrderId());
    }
}
