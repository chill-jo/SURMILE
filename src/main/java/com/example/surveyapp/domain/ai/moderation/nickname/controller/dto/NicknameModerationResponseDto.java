package com.example.surveyapp.domain.ai.moderation.nickname.controller.dto;

import com.example.surveyapp.domain.ai.moderation.config.ModerationResultStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameModerationResponseDto {
    private ModerationResultStatusEnum status;
    private String reason;
}
