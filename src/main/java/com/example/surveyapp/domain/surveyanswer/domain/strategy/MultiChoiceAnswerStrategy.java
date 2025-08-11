package com.example.surveyapp.domain.surveyanswer.domain.strategy;

import com.example.surveyapp.domain.surveyanswer.controller.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultiChoiceAnswerStrategy implements SurveyQuestionStrategy {

    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId) {
        String str = (String) questionAnswer.getAnswer();
        String[] split = str.split(",");
        for (String s : split) {
            try {
                Long number = Long.parseLong(s);
                surveyOptionsAnswerRepository.save(new SurveyOptionsAnswer(surveyAnswer.getId(), questionId, number));
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
