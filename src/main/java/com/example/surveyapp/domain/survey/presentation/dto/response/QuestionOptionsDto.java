package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Question;
import com.example.surveyapp.domain.survey.domain.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOptionsDto {

    private Long id;

    private Long number;

    private String content;

    private QuestionType type;

    private List<OptionResponseDto> options = new ArrayList<>();

    public static QuestionOptionsDto of(Question question, List<OptionResponseDto> options){
        return new QuestionOptionsDto(
                question.getId(),
                question.getNumber(),
                question.getContent(),
                question.getType(),
                options);
    }
}
