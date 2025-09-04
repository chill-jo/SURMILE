package com.example.surveyapp.domain.survey.infrastructure.kafkaListener;

import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemSucceededEvent;
import com.example.surveyapp.domain.survey.application.eventhandler.SurveyEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSurveyPointRedeemEventListener {

    private final SurveyEventHandler eventHandler;

    @KafkaListener(topics = "survey-redeem-failed", groupId = "survey-group", containerFactory = "kafkaListenerContainerFactory")
    public void failedHandle(SurveyPointRedeemFailedEvent event) {
        eventHandler.handlePointRedeemFailEvent(event);
    }

    @KafkaListener(topics = "survey-redeem-succeeded", groupId = "survey-group", containerFactory = "kafkaListenerContainerFactory")
    public void succeedHandle(SurveyPointRedeemSucceededEvent event){
        eventHandler.handlePointRedeemSucceededEvent(event);
    }
}
