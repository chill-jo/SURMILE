package com.example.surveyapp.domain.survey.presentation.dto.response;

import com.example.surveyapp.domain.survey.domain.model.entity.Options;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
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
