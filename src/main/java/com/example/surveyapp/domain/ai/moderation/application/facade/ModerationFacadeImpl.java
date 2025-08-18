package com.example.surveyapp.domain.ai.moderation.application.facade;

import com.example.surveyapp.domain.ai.moderation.application.ModerationService;
import com.example.surveyapp.domain.ai.moderation.domain.model.ModerationTargetType;
import com.example.surveyapp.domain.ai.moderation.domain.vo.ModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationFacadeImpl implements ModerationFacade {
    private final ModerationService moderationService;

    @Override
    public ModerationResult moderateNickname(String nickname) {
        return moderationService.moderate(ModerationTargetType.USER_NICKNAME, nickname);
    }
}
