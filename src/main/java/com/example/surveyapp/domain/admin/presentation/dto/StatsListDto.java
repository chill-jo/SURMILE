package com.example.surveyapp.domain.admin.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class StatsListDto {

    private final String question;

    private final List<StatDto> list = new ArrayList<>();

    public void addStat(StatDto statDto) {
        this.list.add(statDto);
    }

    public static StatsListDto of(String question) {
        return new StatsListDto(question);
    }

}
