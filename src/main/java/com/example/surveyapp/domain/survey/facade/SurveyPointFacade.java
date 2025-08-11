package com.example.surveyapp.domain.survey.facade;

import com.example.surveyapp.domain.point.domain.model.entity.Points;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyPoints;

public interface SurveyPointFacade {
    void decreaseSurveyorPoint(Long userId, Points amount, Long surveyId);
}
