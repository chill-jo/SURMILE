package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionOptionsDto {

    private final Long id;

    private final Long number;

    private final String content;

    private final QuestionType type;

    private List<OptionResponseDto> options = null;

    public static QuestionOptionsDto of(Question question, List<OptionResponseDto> options){
        return new QuestionOptionsDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType(),
                options);
    }
}
