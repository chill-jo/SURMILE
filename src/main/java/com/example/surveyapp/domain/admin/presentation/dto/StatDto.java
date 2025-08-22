package com.example.surveyapp.domain.admin.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StatDto {

    private final Long option;

    private final Long count;

    public static StatDto of(Long option, Long count) {
        return new StatDto(option, count);
    }

}
