package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionResponseDto {

    private final Long id;

    private final Long number;

    private final String content;

    private final QuestionType type;

}
