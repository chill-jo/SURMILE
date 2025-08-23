package com.example.surveyapp.domain.surveyanswer.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;

public interface SurveyAnswerModerationFacade {
    AiModerationResult checkTextAnswerModeration(Long userId, String content);
}
