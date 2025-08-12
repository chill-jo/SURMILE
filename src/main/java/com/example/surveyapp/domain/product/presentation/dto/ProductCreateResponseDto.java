package com.example.surveyapp.domain.product.presentation.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreateResponseDto {

    private final Long id;

    private final String title;

    private final Long price;

    private final Status status;

    public ProductCreateResponseDto(Product product) {
        this.id =product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice().getValue();
        this.status = product.getStatus();
    }

    public static ProductCreateResponseDto from(Product product) {
        return new ProductCreateResponseDto(product);
    }
}
