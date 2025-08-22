package com.example.surveyapp.domain.surveyanswer.domain.strategy;

import com.example.surveyapp.domain.ai.moderation.application.facade.AiModerationFacade;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.QuestionAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswer;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyTextAnswer;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import com.example.surveyapp.domain.surveyanswer.domain.repository.SurveyTextAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubjectAnswerStrategy implements SurveyQuestionStrategy {
    private final SurveyTextAnswerRepository surveyTextAnswerRepository;
    private final AiModerationFacade aiModerationFacade;

    @Override
    public void doSave(QuestionAnswerRequestDto questionAnswer, SurveyAnswer surveyAnswer, Long questionId) {
        String content = (String)questionAnswer.getAnswer();

        if (content != null && !content.isBlank()) {
            aiModerationFacade.checkTextAnswerModeration(content);
        }

        surveyTextAnswerRepository.save(new SurveyTextAnswer(surveyAnswer.getId(), questionId, (String) questionAnswer.getAnswer()));
    }

    @Override
    public boolean isSupport(QuestionType questionType){
        return questionType.isSubjective();
    }

}
