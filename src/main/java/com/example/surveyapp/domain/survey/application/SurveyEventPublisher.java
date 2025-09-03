package com.example.surveyapp.domain.survey.application;

import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;

public interface SurveyEventPublisher {
    void publishEvent(SurveyCreateEvent event);
}
