package com.example.surveyapp.domain.user.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BaseDataListResponseDto {

    private final List<BaseDataResponseDto> list;

}
