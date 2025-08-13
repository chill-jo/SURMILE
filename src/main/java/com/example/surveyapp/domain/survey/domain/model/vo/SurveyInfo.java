package com.example.surveyapp.domain.survey.domain.model.vo;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="point_per_person", nullable = false))
    private SurveyPoints pointPerPerson;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name="total_point", nullable = false))
    private SurveyPoints totalPoint;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Long expectedTime;

    public SurveyInfo(String title, String description, Long maxSurveyee, SurveyPoints pointPerPerson, LocalDateTime deadline, Long expectedTime){
        this.title = title;
        this.description = description;
        this.maxSurveyee = maxSurveyee;
        this.pointPerPerson = pointPerPerson;
        this.totalPoint = SurveyPoints.of(maxSurveyee * pointPerPerson.getValue());
        this.deadline = deadline;
        this.expectedTime = expectedTime;
    }

}
