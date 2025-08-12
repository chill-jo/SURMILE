package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import lombok.Getter;

@Getter
public class OptionResponseDto {

    private final Long id;

    private final Long number;

    private final String content;

    public OptionResponseDto(Long id, Long number, String content){
        this.id = id;
        this.number = number;
        this.content = content;
    }

    public static OptionResponseDto from(Options option){
        return new OptionResponseDto(
                option.getId(),
                option.getNumber(),
                option.getContent()
        );
    }
}
