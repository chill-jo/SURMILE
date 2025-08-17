package com.example.surveyapp.domain.ai.chat.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IndexRequestDto {
    @NotBlank
    private String document;
}
