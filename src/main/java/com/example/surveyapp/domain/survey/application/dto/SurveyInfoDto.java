package com.example.surveyapp.domain.survey.application.dto;

import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SurveyInfoDto {
    private final Long surveyId;
    private final String title;
    private final String description;
    private final Long maxSurveyee;
    private final Long pointPerPerson;
    private final Long totalPoint;
    private final LocalDateTime deadline;
    private final Long expectedTime;

    public static SurveyInfoDto from(Long surveyId, SurveyInfo surveyInfo){
        return new SurveyInfoDto(
                surveyId,
                surveyInfo.getTitle(),
                surveyInfo.getDescription(),
                surveyInfo.getMaxSurveyee(),
                surveyInfo.getPointPerPerson().getValue(),
                surveyInfo.getTotalPoint().getValue(),
                surveyInfo.getDeadline(),
                surveyInfo.getExpectedTime()
        );
    }
}
