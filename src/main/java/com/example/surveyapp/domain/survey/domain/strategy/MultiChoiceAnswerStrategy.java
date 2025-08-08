package com.example.surveyapp.domain.survey.service.strategy;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.survey.domain.repository.SurveyOptionsAnswerRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultiChoiceAnswerStrategy implements SurveyQuestionStrategy {

    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Question question) {
        String str = (String) questionAnswer.getAnswer();
        String[] split = str.split(",");
        for (String s : split) {
            try {
                Long number = Long.parseLong(s);
                surveyOptionsAnswerRepository.save(new SurveyOptionsAnswer(surveyAnswer, question, number));
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.VALIDATION_ERROR);
            }
        }
    }

    @Override
    public boolean isSupport(QuestionType questionType) {
        return questionType.isMultiChoice();
    }
}
