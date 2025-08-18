package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.vo.AiModerationResult;
import com.example.surveyapp.domain.ai.moderation.domain.vo.AiModerationSurveyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiModerationFacadeImpl implements AiModerationFacade {
    private final AiModerationService aiModerationService;

    @Override
    public AiModerationResult checkNicknameModeration(String nickname) {
        return aiModerationService.moderate(AiModerationTargetType.USER_NICKNAME, nickname);
    }

    @Override
    public AiModerationSurveyResult checkSurveyModeration(String title, String description) {
        AiModerationResult titleRes = aiModerationService.moderate(AiModerationTargetType.SURVEY_TITLE, title);
        AiModerationResult descRes  = aiModerationService.moderate(AiModerationTargetType.SURVEY_DESCRIPTION, description);

        return AiModerationSurveyResult.of(titleRes, descRes);
    }

    @Override
    public AiModerationResult checkQuestionModeration(String question) {
        return aiModerationService.moderate(AiModerationTargetType.SURVEY_QUESTION, question);
    }

    @Override
    public AiModerationResult checkOptionsModeration(String options) {
        return aiModerationService.moderate(AiModerationTargetType.SURVEY_OPTIONS, options);
    }

    @Override
    public AiModerationResult checkTextAnswerModeration(String answer) {
        return aiModerationService.moderate(AiModerationTargetType.SURVEY_TEXT_ANSWER, answer);
    }
}
