package com.example.surveyapp.domain.ai.chat.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto {
    @NotBlank
    private String question;
}
