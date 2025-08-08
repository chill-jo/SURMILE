package com.example.surveyapp.domain.survey.service.strategy;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyTextAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.repository.SurveyTextAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubjectAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyTextAnswerRepository surveyTextAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Question question) {
        surveyTextAnswerRepository.save(new SurveyTextAnswer(surveyAnswer, question, (String) questionAnswer.getAnswer()));
    }

    @Override
    public boolean isSupport(QuestionType questionType){
        return questionType.isSubjective();
    }

}
