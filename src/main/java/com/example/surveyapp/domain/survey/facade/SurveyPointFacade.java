package com.example.surveyapp.domain.survey.facade;

public interface SurveyPointFacade {
    void decreaseSurveyorPoint(Long userId, Long amount, Long surveyId);
    void increaseSurveyeePoint(Long userId, Long amount, Long surveyAnswerId);
}
