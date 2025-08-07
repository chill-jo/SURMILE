package com.example.surveyapp.domain.order.facade;


import com.example.surveyapp.domain.product.domain.model.Product;

public interface ProductFacade {
    String findProductTitle(Long id);
    Long findProductPrice(Long id);
}
