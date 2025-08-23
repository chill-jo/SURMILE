package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.ProductPoints;
import com.example.surveyapp.domain.product.domain.model.Status;
import org.springframework.test.util.ReflectionTestUtils;

public class ProductFixtureGenerator {

    public static final String TITLE = "상품명";
    public static final ProductPoints PRICE = ProductPoints.of(2500L);
    public static final String CONTENT = "상품설명";
    public static final Status STATUS = Status.ON_SALE;

    public static Product generateProductFixture() {
        Product product = Product.of(TITLE, PRICE, CONTENT, STATUS);

        return product;

    }
}
