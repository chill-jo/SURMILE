package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.point.domain.event.*;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutboxEnum;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PointOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final PointOutboxRepository pointOutboxRepository;
    private final PointEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
        List<PointOutbox> unpublished = pointOutboxRepository.findByStatusAndPublished(PointOutboxEnum.READY, false);

        for (PointOutbox pointOutbox : unpublished) {
            try {

                eventPublisher.publishEvent(mapEventByAggregateType(pointOutbox));

                pointOutbox.markPublished();

            }catch (OrderException e) {
                pointOutbox.markFailed(MAX_RETRY);
            }
            pointOutboxRepository.save(pointOutbox);
        }

    }

    private Object mapEventByAggregateType(PointOutbox pointOutbox) throws JsonProcessingException {
        if(pointOutbox.getAggregateType().equals("Order-Success")){
            return objectMapper.readValue(pointOutbox.getPayload(), PointRedeemSucceededEvent.class);
        }
        if(pointOutbox.getAggregateType().equals("Survey-Success")){
            return objectMapper.readValue(pointOutbox.getPayload(), SurveyPointRedeemSucceededEvent.class);
        }
        if (pointOutbox.getAggregateType().equals("Payment-Success")) {
            return objectMapper.readValue(pointOutbox.getPayload(), PointChargeSucceededEvent.class);
        }
        if(pointOutbox.getAggregateType().equals("Order-Fail")){
            return objectMapper.readValue(pointOutbox.getPayload(), PointRedeemFailedEvent.class);
        }
        if(pointOutbox.getAggregateType().equals("Survey-Fail")){
            return objectMapper.readValue(pointOutbox.getPayload(), SurveyPointRedeemFailedEvent.class);
        }
        if(pointOutbox.getAggregateType().equals("Payment-Fail")){
            return objectMapper.readValue(pointOutbox.getPayload(), PointChargeFailedEvent.class);
        }
        else{
            return null;
        }
    }
}
