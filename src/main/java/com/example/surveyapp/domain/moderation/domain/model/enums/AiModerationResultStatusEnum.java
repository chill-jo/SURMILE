package com.example.surveyapp.domain.moderation.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiModerationResultStatusEnum {
    APPROVED,
    DENIED;
}