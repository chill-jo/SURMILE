package com.example.surveyapp.domain.product.application.dto;

import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.Getter;

@Getter
public class ProductUpdateResponseDto {

    private final Long id;

    private final String title;

    private final String content;

    private final Long price;

    private final Status status;

    public ProductUpdateResponseDto(Long id, String title, String content, Long price, Status status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.price = price;
        this.status = status;
    }
}
