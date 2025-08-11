package com.example.surveyapp.domain.survey.domain.model.entity;

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

    @ManyToOne
    @JoinColumn(name = "survey_answer_id", nullable = false)
    private SurveyAnswer surveyAnswerId;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question questionId;

    @Column(nullable = false)
    private String content;

    public SurveyTextAnswer(SurveyAnswer surveyAnswer, Question question, String content) {
        this.surveyAnswerId = surveyAnswer;
        this.questionId = question;
        this.content = content;
    }
}