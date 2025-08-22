package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;

public interface AiModerationFacade {
    AiModerationResult checkNicknameModeration(Long userId, String Email, String nickname);
    AiModerationResult checkTitleModeration(Long userId, String title);
    AiModerationResult checkDescriptionModeration(Long userId, String description);
    AiModerationResult checkQuestionModeration(Long userId, String question);
    AiModerationResult checkOptionsModeration(Long userId, String options);
    AiModerationResult checkTextAnswerModeration(Long userId, String content);
}
