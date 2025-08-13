package com.example.surveyapp.domain.survey.application.dto;

import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionIdAndTypeDto {
    private final Long questionId;
    private final QuestionType questionType;

    public static QuestionIdAndTypeDto of(Long questionId, QuestionType questionType){
        return new QuestionIdAndTypeDto(questionId, questionType);
    }
}
