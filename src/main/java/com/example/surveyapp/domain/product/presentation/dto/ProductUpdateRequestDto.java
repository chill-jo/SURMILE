package com.example.surveyapp.domain.product.presentation.dto;

import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUpdateRequestDto {

    private final String title;

    private final Long price;

    private final String content;

    private final Status status;
}
