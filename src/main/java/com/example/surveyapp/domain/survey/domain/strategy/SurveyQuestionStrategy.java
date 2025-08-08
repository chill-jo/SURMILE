package com.example.surveyapp.domain.survey.service.strategy;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;

public interface SurveyQuestionStrategy {
    void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Question question);

    boolean isSupport(QuestionType questionType);

    default void saveAnswerWithStrategy(SurveyAnswerRequestDto requestDto){
        requestDto.getAnswers().forEach(questionAnswer -> {
            Question question = questionRepository.findById(questionAnswer.getNumber()).orElseThrow(
                    () -> new CustomException(ErrorCode.QUESTION_NOT_FOUND)
            );

            // TODO 질문 타입에 따라 각 질문 응답을 처리할 수 있는 전략을 별도로 구성해서 처리하면 어떨까
            surveyQuestionStrategies.stream()
                    .filter(it -> it.isSupport(question.getType()))
                    .findFirst()
                    .orElseThrow()
                    .doSave(questionAnswer, surveyAnswer, question);
        });
    }
}
