package com.example.surveyapp.domain.surveyanswer.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyAnswerRequestDto {

    private final List<QuestionAnswerRequestDto> answers;

}
