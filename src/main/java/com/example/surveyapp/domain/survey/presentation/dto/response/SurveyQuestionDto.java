package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class SurveyQuestionDto {
    private final Long id;

    private final String title;

    private final String description;

    private final Long maxSurveyee;

    private final Long pointPerPerson;

    private final Long totalPoint;

    private final LocalDateTime deadline;

    private final Long expectedTime;

    private List<QuestionOptionsDto> questions;

    public void addQuestion(QuestionOptionsDto questionOptionsDto) {
        this.questions.add(questionOptionsDto);
    }

    public static SurveyQuestionDto of(Survey survey) {
        return new SurveyQuestionDto(
                survey.getId(),
                survey.getSurveyInfo().getTitle(),
                survey.getSurveyInfo().getDescription(),
                survey.getSurveyInfo().getMaxSurveyee(),
                survey.getSurveyInfo().getPointPerPerson().getValue(),
                survey.getSurveyInfo().getTotalPoint().getValue(),
                survey.getSurveyInfo().getDeadline(),
                survey.getSurveyInfo().getExpectedTime(),
                new ArrayList<>()
        );
    }
}
