package com.example.surveyapp.domain.survey.facade;

import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;

public interface SurveyPointFacade {
    void decreaseSurveyorPoint(Long userId, PointPoints amount, Long surveyId);
}
