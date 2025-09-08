package com.example.surveyapp.domain.order.application;

public interface ProductClient {
    ProductClientResponseDto getProduct(Long userUId, Long productId);
}
