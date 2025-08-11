package com.example.surveyapp.domain.surveyanswer.domain.strategy;

import com.example.surveyapp.domain.surveyanswer.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;

public interface SurveyQuestionStrategy {
    void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId);

    boolean isSupport(QuestionType questionType);
}
