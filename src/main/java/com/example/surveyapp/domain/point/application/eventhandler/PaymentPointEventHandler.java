package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.PointChargeFailedEvent;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointChargeEvent(PointChargeEvent event){
        try{
            pointEarnRedeemService.increaseSurveyorPoint(
                    event.getUserId(),
                    event.getPaymentId(),
                    PointBalance.of(event.getAmount()));

        }catch(Exception e){
            eventPublisher.publishEvent(
                    new PointChargeFailedEvent(
                            event.getUserId(),
                            event.getPaymentId()
                    )
            );
        }
    }

}
