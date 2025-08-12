package com.example.surveyapp.domain.survey.application.dto;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionDto {
    private final Long questionId;
    private final Long number;
    private final String content;

    public static QuestionDto of(Long questionId, Long number, String content){
        return new QuestionDto(questionId, number, content);
    }
    public static QuestionDto from(Question question){
        return new QuestionDto(
                question.getId(),
                question.getNumber(),
                question.getContent()
        );
    }
}
