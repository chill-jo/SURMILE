package com.example.surveyapp.domain.surveyanswer.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "survey_text_answer")
@AllArgsConstructor
@NoArgsConstructor
public class SurveyTextAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long surveyAnswerId;

    private Long questionId;

    @Column(nullable = false)
    private String content;

    public SurveyTextAnswer(Long surveyAnswerId, Long questionId, String content) {
        this.surveyAnswerId = surveyAnswerId;
        this.questionId = questionId;
        this.content = content;
    }
}