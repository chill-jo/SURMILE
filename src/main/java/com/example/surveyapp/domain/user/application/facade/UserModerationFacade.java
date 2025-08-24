package com.example.surveyapp.domain.user.application.facade;

import com.example.surveyapp.domain.moderation.domain.model.vo.AiModerationResult;

public interface UserModerationFacade {
    AiModerationResult checkNicknameModeration(Long userId, String Email, String nickname);
}
