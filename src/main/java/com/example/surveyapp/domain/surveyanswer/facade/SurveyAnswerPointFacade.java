package com.example.surveyapp.domain.surveyanswer.facade;

import com.example.surveyapp.domain.point.domain.model.entity.Points;

public interface SurveyAnswerPointFacade {
    void increaseSurveyeePoint(Long userId, Points amount, Long surveyAnswerId);
}
