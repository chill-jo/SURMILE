package com.example.surveyapp.domain.order.facade;


import com.example.surveyapp.domain.product.controller.dto.ProductInfoDto;


public interface ProductFacade {
    ProductInfoDto findProductInfo(Long id);

}
