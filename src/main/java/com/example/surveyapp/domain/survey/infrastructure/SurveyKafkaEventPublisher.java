package com.example.surveyapp.domain.survey.infrastructure;

import com.example.surveyapp.domain.survey.application.SurveyEventPublisher;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyKafkaEventPublisher implements SurveyEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishEvent(SurveyCreateEvent event) {
        kafkaTemplate.send("Survey-created", event);
    }
}
