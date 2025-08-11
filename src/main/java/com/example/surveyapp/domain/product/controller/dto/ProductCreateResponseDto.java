package com.example.surveyapp.domain.product.controller.dto;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateResponseDto {

    private Long id;

    private String title;

    private Long price;

    private Status status;

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
