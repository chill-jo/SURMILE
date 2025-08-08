package com.example.surveyapp.domain.product.controller.dto;

import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductInfoDto {

    private String title;

    private Long price;

    private Status status;

    private String statusName;

    public ProductInfoDto(String title, Long price, Status status) {
        this.title = title;
        this.price = price;
        this.status = status;
        this.statusName = status.getStatus();
    }
}
