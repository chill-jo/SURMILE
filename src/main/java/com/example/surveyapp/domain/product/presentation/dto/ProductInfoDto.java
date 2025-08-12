package com.example.surveyapp.domain.product.presentation.dto;

import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductInfoDto {

    private final String title;

    private final Long price;

    private final Status status;

    private final String statusName;

    public ProductInfoDto(String title, Long price, Status status) {
        this.title = title;
        this.price = price;
        this.status = status;
        this.statusName = status.getStatus();
    }
}
