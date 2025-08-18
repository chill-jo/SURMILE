package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationSurveyResult;

public interface AiModerationFacade {
    AiModerationResult checkNicknameModeration(String nickname);
    AiModerationSurveyResult checkSurveyModeration(String title, String description);
    AiModerationResult checkQuestionModeration(String question);
    AiModerationResult checkOptionsModeration(String options);
    AiModerationResult checkTextAnswerModeration(String content);
}
