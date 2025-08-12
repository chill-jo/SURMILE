package com.example.surveyapp.domain.surveyanswer.presentation.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 설문 통계 depth 1
 */
@Getter
@RequiredArgsConstructor
public class SurveyStatisticsDto {
    private final Long surveyId;
    private final String title;
    private final String description;
    private final Long maxSurveyee;
    private final Long pointPerPerson;
    private final Long totalPoint;
    private final LocalDateTime deadline;
    private final Long expectedTime;
    private final Long count;

    private List<SurveyStatisticsQuestionDto> questions;

    public void addQuestionDtoList(List<SurveyStatisticsQuestionDto> questionDtoList){
        questions = questionDtoList;
    }
    public static SurveyStatisticsDto of(
            Long surveyId,
            String title,
            String description,
            Long maxSurveyee,
            Long pointPerPerson,
            Long totalPoint,
            LocalDateTime deadline,
            Long expectedTime,
            Long count){
        return new SurveyStatisticsDto(surveyId, title, description,
                maxSurveyee, pointPerPerson, totalPoint,
                deadline, expectedTime, count);
    }


}
