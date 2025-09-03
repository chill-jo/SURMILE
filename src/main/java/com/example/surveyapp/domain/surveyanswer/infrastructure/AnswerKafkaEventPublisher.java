package com.example.surveyapp.domain.surveyanswer.infrastructure;

import com.example.surveyapp.domain.surveyanswer.application.AnswerEventPublisher;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerKafkaEventPublisher implements AnswerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void answerPublishEvent(SurveyAnswerEvent event) {
        kafkaTemplate.send("SurveyAnswer-created", event);
    }

    @Override
    public void donePublishEvent(SurveyDoneEvent event) {
        kafkaTemplate.send("SurveyAnswer-Done", event);
    }
}
