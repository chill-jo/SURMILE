package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.point.application.eventhandler.SurveyPointEventHandler;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSurveyEventListener {
    private final SurveyPointEventHandler surveyPointEventHandler;

    @KafkaListener(topics = "survey-created" , groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SurveyCreateEvent event){
        surveyPointEventHandler.handleSurveyCreateEvent(event);
    }
}
