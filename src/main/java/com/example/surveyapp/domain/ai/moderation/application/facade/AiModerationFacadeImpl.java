package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.AiModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.vo.AiModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiModerationFacadeImpl implements AiModerationFacade {
    private final AiModerationService aiModerationService;

    @Override
    public AiModerationResult moderateNickname(String nickname) {
        return aiModerationService.moderate(AiModerationTargetType.USER_NICKNAME, nickname);
    }
}
