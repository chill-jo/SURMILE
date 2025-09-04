package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.PointChargeFailedEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;
   private final ObjectMapper objectMapper;
    private final PointOutboxRepository pointOutboxRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointChargeEvent(PointChargeEvent event){
        try{
            pointEarnRedeemService.increaseSurveyorPoint(
                    event.getUserId(),
                    event.getPaymentId(),
                    PointBalance.of(event.getAmount())
            );

        }catch(Exception e){
            PointChargeFailedEvent pointFailedEvent = new PointChargeFailedEvent(event.getUserId(), event.getPaymentId());
            publishOutbox(pointFailedEvent);
        }
    }

    private void publishOutbox(PointChargeFailedEvent event){
        PointOutbox pointOutbox = PointOutbox.of(
                "Payment-Fail",
                event.getPaymentId(),
                toJson(event)
        );
        pointOutboxRepository.save(pointOutbox);
    }

    private String toJson(Object event){
        try{
            return objectMapper.writeValueAsString(event);
        }catch(JsonProcessingException e){
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }
    }

}
