package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;

public interface AiModerationFacade {
    AiModerationResult checkNicknameModeration(String nickname);
    AiModerationResult checkTitleModeration(String title);
    AiModerationResult checkDescriptionModeration(String description);
    AiModerationResult checkQuestionModeration(String question);
    AiModerationResult checkOptionsModeration(String options);
    AiModerationResult checkTextAnswerModeration(String content);
}
