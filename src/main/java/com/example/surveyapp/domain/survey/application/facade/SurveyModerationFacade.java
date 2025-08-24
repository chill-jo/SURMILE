package com.example.surveyapp.domain.survey.application.facade;

import com.example.surveyapp.domain.moderation.domain.model.vo.AiModerationResult;

public interface SurveyModerationFacade {
    AiModerationResult checkTitleModeration(Long userId, String title);
    AiModerationResult checkDescriptionModeration(Long userId, String description);
    AiModerationResult checkQuestionModeration(Long userId, String question);
    AiModerationResult checkOptionsModeration(Long userId, String options);
}
