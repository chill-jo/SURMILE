package com.example.surveyapp.domain.surveyanswer.facade;

import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;

public interface SurveyAnswerPointFacade {
    void increaseSurveyeePoint(Long userId, PointPoints amount, Long surveyAnswerId);
}
