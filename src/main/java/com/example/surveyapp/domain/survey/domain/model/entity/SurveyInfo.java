package com.example.surveyapp.domain.survey.domain.model.entity;

import com.example.surveyapp.domain.survey.controller.dto.request.SurveyUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyInfo {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long maxSurveyee;

    @Column(nullable = false)
    private Long pointPerPerson;

    @Column(nullable = false)
    private Long totalPoint;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Long expectedTime;

    public SurveyInfo(String title, String description, Long maxSurveyee, Long pointPerPerson, LocalDateTime deadline, Long expectedTime){
        this.title = title;
        this.description = description;
        this.maxSurveyee = maxSurveyee;
        this.pointPerPerson = pointPerPerson;
        this.totalPoint = maxSurveyee * pointPerPerson;
        this.deadline = deadline;
        this.expectedTime = expectedTime;
    }

    public SurveyInfo updateSurveyInfo(SurveyUpdateRequestDto requestDto){

        return new SurveyInfo(
            requestDto.getTitle() != null ? requestDto.getTitle() : this.title,
            requestDto.getDescription() != null ? requestDto.getDescription() : this.description,
            requestDto.getMaxSurveyee() != null ? requestDto.getMaxSurveyee() : this.maxSurveyee,
            requestDto.getPointPerPerson() != null ? requestDto.getPointPerPerson() : this.pointPerPerson,
            requestDto.getDeadline() != null ? requestDto.getDeadline() : this.deadline,
            requestDto.getExpectedTime() != null ? requestDto.getExpectedTime() : this.expectedTime
        );
    }

}
