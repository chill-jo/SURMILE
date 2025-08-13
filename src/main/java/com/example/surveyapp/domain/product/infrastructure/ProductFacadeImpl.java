package com.example.surveyapp.domain.product.infrastructure;

import com.example.surveyapp.domain.order.application.facade.ProductFacade;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.exception.ProductErrorCode;
import com.example.surveyapp.domain.product.exception.ProductException;
import com.example.surveyapp.domain.product.presentation.dto.ProductInfoDto;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacadeImpl implements ProductFacade {

    private final ProductRepository productRepository;
    @Override
    public ProductInfoDto findProductInfo(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.NOT_FOUND_PRODUCT));
        product.productPriceZeroOrThrow();
        product.getStatusStoppedSaleOrThrow();
        return new ProductInfoDto(
                product.getTitle(),
                product.getPrice().getValue(),
                product.getStatus()
        );
    }
}
