package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.domain.vo.ModerationResult;

public interface ModerationFacade {
    ModerationResult moderateNickname(Long userId, String nickname);
}
