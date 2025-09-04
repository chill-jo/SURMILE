package com.example.surveyapp.domain.point.infrastructure.KafkaListener;

import com.example.surveyapp.domain.point.application.eventhandler.SurveyAnswerEventHandler;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAnswerPointEventListener {

    private final SurveyAnswerEventHandler surveyAnswerEventHandler;

    @KafkaListener(topics = "answer-point-created", groupId = "point-group", containerFactory = "kafkaListenerContainerFactory")
    public void handle(SurveyAnswerEvent event) {
        surveyAnswerEventHandler.handleAnswerCreateEvent(event);
    }

}
