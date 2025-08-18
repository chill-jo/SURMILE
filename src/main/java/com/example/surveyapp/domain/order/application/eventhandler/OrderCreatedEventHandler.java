package com.example.surveyapp.domain.order.application.eventhandler;

import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.repository.OrderRepository;
import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.point.domain.model.entity.event.PointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.model.entity.event.PointRedeemSucceededEvent;
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

    /**
     * 이벤트 발행 수신 성공시 결제 상태 확정 이벤트
     * @param event  -> 포인트 차감 성공 이벤트 로직
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void PointSucceedEvent(PointRedeemSucceededEvent event) {
        log.info("이벤트 성공 컨펌으로 변경 메서드");
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.getOrderId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));
        order.confirm();
            }


    /**
     * 이벤트 발행 수신 실패 시 결제 상태 확정 이벤트
     * @param event
     */
    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void PointFailedEvent(PointRedeemFailedEvent event) {
        log.info("이벤트 실패 캔슬로 변경 메서드");

        Order order = orderRepository.findByIdAndIsDeletedFalse(event.getOrderId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        order.cancel();
    }

}
