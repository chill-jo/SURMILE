package com.example.surveyapp.domain.surveyanswer.domain.repository;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;

import java.util.List;

public interface SurveyAnswerRepository{

    SurveyAnswer save(SurveyAnswer surveyAnswer);

    List<SurveyAnswer> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    Long countBySurveyId(Long surveyId);

}
