package com.example.surveyapp.domain.surveyanswer.domain.strategy;

import com.example.surveyapp.domain.surveyanswer.exception.AnswerErrorCode;
import com.example.surveyapp.domain.surveyanswer.exception.AnswerException;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
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
                throw new AnswerException(AnswerErrorCode.VALIDATION_ERROR);
            }
        }
    }

    @Override
    public boolean isSupport(QuestionType questionType) {
        return questionType.isMultiChoice();
    }
}
