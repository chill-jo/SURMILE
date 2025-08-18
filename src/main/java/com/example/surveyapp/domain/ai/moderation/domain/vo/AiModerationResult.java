package com.example.surveyapp.domain.ai.moderation.domain.vo;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationResultStatusEnum;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModerationResult {
    private AiModerationResultStatusEnum status;
    private Long moderationId;

    private AiModerationResult(AiModerationResultStatusEnum status, Long moderationId) {
        this.status = status;
        this.moderationId = moderationId;
    }
}
