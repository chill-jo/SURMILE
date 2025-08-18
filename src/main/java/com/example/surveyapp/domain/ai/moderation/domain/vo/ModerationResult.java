package com.example.surveyapp.domain.ai.moderation.domain.vo;

import com.example.surveyapp.domain.ai.moderation.domain.model.ModerationResultStatusEnum;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModerationResult {
    private ModerationResultStatusEnum status;
    private Long moderationId;

    private ModerationResult(ModerationResultStatusEnum status, Long moderationId) {
        this.status = status;
        this.moderationId = moderationId;
    }
}
