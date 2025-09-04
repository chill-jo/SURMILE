package com.example.surveyapp.domain.survey.infrastructure.kafkaListener;

import com.example.surveyapp.domain.survey.application.eventhandler.SurveyEventHandler;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSurveyDoneEventListener {

    private final SurveyEventHandler eventHandler;

    @KafkaListener(topics = "survey-answer-done", groupId = "survey-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SurveyDoneEvent event){
        eventHandler.handleSurveyDoneEvent(event);
    }

}
