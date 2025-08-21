package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
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
    public AiModerationResult checkTitleModeration(String title) {
        return aiModerationService.moderate(AiModerationTargetType.SURVEY_TITLE, title);
    }

    @Override
    public AiModerationResult checkDescriptionModeration(String description){
        return aiModerationService.moderate(AiModerationTargetType.SURVEY_DESCRIPTION, description);
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
