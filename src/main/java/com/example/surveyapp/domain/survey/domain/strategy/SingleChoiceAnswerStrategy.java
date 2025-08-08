package com.example.surveyapp.domain.survey.service.strategy;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.repository.SurveyOptionsAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleChoiceAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Question question) {
        Number answer = (Number) questionAnswer.getAnswer();
        surveyOptionsAnswerRepository.save(new SurveyOptionsAnswer(surveyAnswer, question, answer.longValue()));
    }

    @Override
    public boolean isSupport(QuestionType questionType) {
        return questionType.isSingleChoice();
    }

}
