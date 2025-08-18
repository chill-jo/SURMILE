package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.vo.AiModerationResult;

public interface AiModerationFacade {
    AiModerationResult moderateNickname(String nickname);
}
