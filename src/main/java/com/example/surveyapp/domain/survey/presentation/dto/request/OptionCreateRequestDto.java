package com.example.surveyapp.domain.survey.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class OptionCreateRequestDto {

    @NotNull(message = "선택지 번호는 필수입니다.")
    private final Long number;

    @Length(min = 1, max = 255, message = "선택지 내용은 1~255자 사이여야 합니다.")
    @NotBlank(message = "선택지 내용은 필수입니다.")
    private final String content;

}
