package com.example.surveyapp.domain.survey.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class OptionUpdateRequestDto {

    private final Long number;

    @Length(min = 1, max = 255, message = "선택지 내용은 1~255자 사이여야 합니다.")
    private final String content;
}
