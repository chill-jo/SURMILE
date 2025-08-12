package com.example.surveyapp.domain.user.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class BaseDataListRequestDto {

    @NotNull(message = "기초 정보 답을 입력하세요.")
    @Valid
    private List<BaseDataRequestDto> list;

}
