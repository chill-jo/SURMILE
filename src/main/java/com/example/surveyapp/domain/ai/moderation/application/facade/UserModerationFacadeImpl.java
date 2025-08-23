package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.enums.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.model.vo.AiModerationResult;
import com.example.surveyapp.domain.user.application.facade.UserModerationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserModerationFacadeImpl implements UserModerationFacade {
    private final AiModerationService aiModerationService;

    @Override
    public AiModerationResult checkNicknameModeration(Long userId, String email, String nickname) {
        return aiModerationService.moderate(userId, email, AiModerationTargetType.USER_NICKNAME, nickname);
    }
}
