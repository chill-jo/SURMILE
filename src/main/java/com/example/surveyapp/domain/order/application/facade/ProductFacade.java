package com.example.surveyapp.domain.order.application.facade;


import com.example.surveyapp.domain.product.presentation.dto.ProductInfoDto;


public interface ProductFacade {
    ProductInfoDto findProductInfo(Long id);

}
