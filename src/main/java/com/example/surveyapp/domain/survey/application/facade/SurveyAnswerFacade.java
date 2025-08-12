package com.example.surveyapp.domain.survey.application.facade;

public interface SurveyAnswerFacade {
    void validateParticipated(Long userId, Long surveyId);
    Long getParticipatedCount(Long surveyId);
}
