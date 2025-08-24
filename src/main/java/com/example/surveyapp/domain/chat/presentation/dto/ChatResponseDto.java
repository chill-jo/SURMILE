package com.example.surveyapp.domain.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {
    private String answer;

    public static ChatResponseDto of(String answer) {
        return new ChatResponseDto(answer);
    }
}
