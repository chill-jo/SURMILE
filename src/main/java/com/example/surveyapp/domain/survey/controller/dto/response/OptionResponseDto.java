package com.example.surveyapp.domain.survey.controller.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionResponseDto {

    private Long id;

    private Long number;

    private String content;

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
