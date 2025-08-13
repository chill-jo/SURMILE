package com.example.surveyapp.domain.ai.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatResponseDto {
    private String answer;

    public static ChatResponseDto of(String answer) {
        return new ChatResponseDto(answer);
    }
}
