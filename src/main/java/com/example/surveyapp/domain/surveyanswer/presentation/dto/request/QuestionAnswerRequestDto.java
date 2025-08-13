package com.example.surveyapp.domain.surveyanswer.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QuestionAnswerRequestDto {

    @NotNull(message = "번호는 필수입니다.")
    private Long number;

    @NotNull(message = "응답은 필수입니다.")
    private Object answer;
}
