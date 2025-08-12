package com.example.surveyapp.domain.survey.presentation.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageQuestionResponseDto<T> {

    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;

    public PageQuestionResponseDto(Page<T> page){
        content = page.getContent();
        totalElements = page.getTotalElements();
        totalPages = page.getTotalPages();
        size = page.getSize();
        number = page.getNumber();
    }

}

