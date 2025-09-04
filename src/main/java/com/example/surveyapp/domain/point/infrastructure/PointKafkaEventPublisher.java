package com.example.surveyapp.domain.point.infrastructure;

import com.example.surveyapp.domain.point.application.PointEventPublisher;
import com.example.surveyapp.domain.point.domain.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointKafkaEventPublisher implements PointEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishEvent(Object event) {
        if ( event instanceof PointChargeFailedEvent e) {
            kafkaTemplate.send("point-charge-failed", event);
        } else if (event instanceof PointChargeSucceededEvent e) {
            kafkaTemplate.send("point-charge-succeeded", event);
        } else if (event instanceof PointRedeemFailedEvent e) {
            kafkaTemplate.send("order-redeem-failed", event);
        } else if (event instanceof PointRedeemSucceededEvent e) {
            kafkaTemplate.send("order-redeem-succeeded", event);
        } else if (event instanceof SurveyPointRedeemFailedEvent e) {
            kafkaTemplate.send("survey-redeem-failed", event);
        } else if (event instanceof SurveyPointRedeemSucceededEvent e) {
            kafkaTemplate.send("survey-redeem-succeeded", event);
        }
    }

}
