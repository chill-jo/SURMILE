package com.example.surveyapp.domain.surveyanswer.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "survey_options_answer")
@AllArgsConstructor
@NoArgsConstructor
public class SurveyOptionsAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long surveyAnswerId;

    private Long questionId;

    @Column(nullable = false)
    private Long number;

    public SurveyOptionsAnswer(Long surveyAnswerId, Long questionId, Long number) {
        this.surveyAnswerId = surveyAnswerId;
        this.questionId = questionId;
        this.number = number;
    }
}
