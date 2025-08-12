package com.example.surveyapp.domain.surveyanswer.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionAnswerRequestDto {

    @NotBlank(message = "번호는 필수입니다.")
    private final Long number;

    @NotBlank(message = "응답은 필수입니다.")
    private final Object answer;

}
