package com.example.surveyapp.domain.survey.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyAnswerRequestDto {

    private List<QuestionAnswerRequestDto> answers;

}
