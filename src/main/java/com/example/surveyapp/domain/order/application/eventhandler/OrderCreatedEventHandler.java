package com.example.surveyapp.domain.order.application.eventhandler;

import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.repository.OrderRepository;
import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.point.domain.event.PointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.event.PointRedeemSucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventHandler {

    private final OrderRepository orderRepository;
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pointSucceedEvent(PointRedeemSucceededEvent event) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.getTargetId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));
        order.confirm();

    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void pointFailedEvent(PointRedeemFailedEvent event) {
           Order order = orderRepository.findByIdAndIsDeletedFalse(event.getTargetId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        order.cancel();
    }

}
