package com.example.surveyapp.domain.ai.moderation.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModerationResultStatusEnum {
    APPROVED,
    DENIED;
}