package com.example.surveyapp.domain.surveyanswer.domain.repository;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyOptionsAnswer;

public interface SurveyOptionsAnswerRepository {
    Long countByQuestionIdAndNumber(Long questionId, Long number);
    SurveyOptionsAnswer save(SurveyOptionsAnswer surveyOptionsAnswer);
}
