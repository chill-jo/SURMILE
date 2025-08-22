package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.PointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPointEventHandler {

    private final PointOutboxRepository pointOutboxRepository;
    private final PointEarnRedeemService pointEarnRedeemService;
    private final ObjectMapper objectMapper;

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
            PointRedeemFailedEvent pointFailedEvent = new PointRedeemFailedEvent(event.getOrderId(), event.getUserId());
            publishOutbox(pointFailedEvent);
        }
        catch (Exception e){
            PointRedeemFailedEvent pointFailedEvent = new PointRedeemFailedEvent(event.getOrderId(), event.getUserId());
            publishOutbox(pointFailedEvent);
        }
    }

    private void publishOutbox(PointRedeemFailedEvent event){
        PointOutbox pointOutbox = PointOutbox.of(
                "Order-Fail",
                event.getTargetId(),
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
