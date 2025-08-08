package com.example.surveyapp.domain.survey.controller.dto.request;

import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateRequestDto {

    private Long number;

    @Length(min = 5, max = 255, message = "질문 내용은 5~255자 사이여야 합니다.")
    private String content;

    private QuestionType type;
}
