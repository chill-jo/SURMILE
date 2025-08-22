package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.point.domain.event.PointChargeSucceededEvent;
import com.example.surveyapp.domain.point.domain.event.PointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutboxEnum;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PointOutboxScheduler {

    private final ObjectMapper objectMapper;
    private final PointOutboxRepository pointOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void publishOutboxEvents() throws Exception {
        List<PointOutbox> unpublished = pointOutboxRepository.findByStatusAndPublished(PointOutboxEnum.READY.READY, false);

        for (PointOutbox pointOutbox : unpublished) {
            try {
                if (pointOutbox.getAggregateType().equals("Order")){
                    eventPublisher.publishEvent(objectMapper.readValue(pointOutbox.getPayload(), PointRedeemSucceededEvent.class));
                }
                if (pointOutbox.getAggregateType().equals("Survey")) {
                    eventPublisher.publishEvent(objectMapper.readValue(pointOutbox.getPayload(), SurveyPointRedeemSucceededEvent.class));
                }
                if (pointOutbox.getAggregateType().equals("Payment")) {
                    eventPublisher.publishEvent(objectMapper.readValue(pointOutbox.getPayload(), PointChargeSucceededEvent.class));
                }

                pointOutbox.markPublished();

            }catch (OrderException e) {
                pointOutbox.markFailed(MAX_RETRY);
            }
            pointOutboxRepository.save(pointOutbox);
        }

    }
}
