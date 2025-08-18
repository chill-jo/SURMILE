package com.example.surveyapp.domain.ai.moderation.domain.vo;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationResultStatusEnum;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModerationResult {
    private Long moderationId;
    private AiModerationResultStatusEnum status;

    @Builder(access = AccessLevel.PRIVATE)
    private AiModerationResult(Long moderationId, AiModerationResultStatusEnum status) {
        this.moderationId = moderationId;
        this.status = status;
    }

    public static AiModerationResult of(Long moderationId, AiModerationResultStatusEnum status) {
        return AiModerationResult.builder()
                .moderationId(moderationId)
                .status(status)
                .build();
    }
}
