package com.example.surveyapp.domain.survey.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OptionUpdateRequestDto {

    private Long number;

    @Length(min = 1, max = 255, message = "선택지 내용은 1~255자 사이여야 합니다.")
    private String content;
}
