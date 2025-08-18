package com.example.surveyapp.domain.ai.moderation.domain.vo;

import com.example.surveyapp.domain.ai.moderation.domain.model.AiModerationResultStatusEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModerationSurveyResult {
    private Long moderationId;
    AiModerationResult titleResult;       // SURVEY_TITLE 결과
    AiModerationResult descriptionResult; // SURVEY_DESCRIPTION 결과

    @Builder(access = AccessLevel.PRIVATE)
    private AiModerationSurveyResult(AiModerationResult titleResult, AiModerationResult descriptionResult) {
        this.titleResult = titleResult;
        this.descriptionResult = descriptionResult;
    }

    public static AiModerationSurveyResult of(AiModerationResult titleResult, AiModerationResult descriptionResult) {
        return AiModerationSurveyResult.builder()
                .titleResult(titleResult)
                .descriptionResult(descriptionResult)
                .build();
    }

    public AiModerationResultStatusEnum allStatus() {
        if (titleResult != null && titleResult.getStatus() == AiModerationResultStatusEnum.DENIED)
            return AiModerationResultStatusEnum.DENIED;
        if (descriptionResult != null && descriptionResult.getStatus() == AiModerationResultStatusEnum.DENIED)
            return AiModerationResultStatusEnum.DENIED;
        return AiModerationResultStatusEnum.APPROVED;
    }
}
