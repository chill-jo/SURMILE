package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.PointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreateEvent(OrderCreateEvent event) {
        try {
            pointEarnRedeemService.decreasePoint(
                    event.getUserId(),
                    PointBalance.of(event.getTotalAmount()),
                    event.getOrderId());

        }
        catch (PointException e) {
            eventPublisher.publishEvent(new PointRedeemFailedEvent(
                    event.getUserId(),
                    event.getOrderId()
                    ));
            log.info("포인트 차감 실패(PointException) – orderId={}, userId={}, reason={}",
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
                    log.info("포인트 차감 메서드 금액 실패 후 이밴트 발행");

        }
        catch (Exception e){
            eventPublisher.publishEvent(new PointRedeemFailedEvent(
                    event.getUserId(),
                    event.getOrderId()
                    ));
            log.info("포인트 차감 실패(기타) – orderId={}, userId={}", event.getOrderId(), event.getUserId(), e);

            log.info("포인트 차감 메서드 실패 후 이밴트 발행");
//메서드 실패 이유-> 인터넷장애, 메서드 실패, 트랜잭션 실패
        }

    }
}
