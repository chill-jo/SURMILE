package com.example.surveyapp.domain.product.infra;

import com.example.surveyapp.domain.order.facade.ProductFacade;
import com.example.surveyapp.domain.product.controller.dto.ProductInfoDto;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.repository.ProductRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacadeImpl implements ProductFacade {

    private final ProductRepository productRepository;
    @Override
    public ProductInfoDto findProductInfo(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        return new ProductInfoDto(
                product.getTitle(),
                product.getPrice().getValue(),
                product.getStatus()
        );
    }
}
