package com.example.surveyapp.domain.surveyanswer.domain.strategy;

import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyOptionsAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyOptionsAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleChoiceAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyOptionsAnswerRepository surveyOptionsAnswerRepository;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId) {
        Number answer = (Number) questionAnswer.getAnswer();
        surveyOptionsAnswerRepository.save(new SurveyOptionsAnswer(surveyAnswer.getId(), questionId, answer.longValue()));
    }

    @Override
    public boolean isSupport(QuestionType questionType) {
        return questionType.isSingleChoice();
    }

}
