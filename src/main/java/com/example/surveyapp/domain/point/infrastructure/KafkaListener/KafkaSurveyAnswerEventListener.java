package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.point.application.eventhandler.SurveyPointEventHandler;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSurveyAnswerEventListener {
    private final SurveyPointEventHandler eventHandler;

    @KafkaListener(topics = "survey-answer-created", groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SurveyAnswerEvent event){
        eventHandler.handleSurveyAnswerEvent(event);
    }
}
