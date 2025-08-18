package com.example.surveyapp.domain.ai.moderation.domain.vo;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationResultStatusEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModerationSurveyResult {
    private Long moderationId;
    AiModerationResult titleResult;       // SURVEY_TITLE 결과
    AiModerationResult descriptionResult; // SURVEY_DESCRIPTION 결과

    public AiModerationResultStatusEnum allStatus() {
        if (titleResult != null && titleResult.getStatus() == AiModerationResultStatusEnum.DENIED)
            return AiModerationResultStatusEnum.DENIED;
        if (descriptionResult != null && descriptionResult.getStatus() == AiModerationResultStatusEnum.DENIED)
            return AiModerationResultStatusEnum.DENIED;
        return AiModerationResultStatusEnum.APPROVED;
    }
}
