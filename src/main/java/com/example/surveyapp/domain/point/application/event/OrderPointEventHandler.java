package com.example.surveyapp.domain.point.application.event;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreateEvent(OrderCreateEvent event) {
        pointEarnRedeemService.decreasePoint(
                event.getUserId(),
                PointBalance.of(event.getTotalAmount()),
                event.getOrderId());
    }
}
