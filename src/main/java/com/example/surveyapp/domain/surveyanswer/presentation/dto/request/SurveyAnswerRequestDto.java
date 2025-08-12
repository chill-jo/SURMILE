package com.example.surveyapp.domain.surveyanswer.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SurveyAnswerRequestDto {

    private final List<QuestionAnswerRequestDto> answers;

}
