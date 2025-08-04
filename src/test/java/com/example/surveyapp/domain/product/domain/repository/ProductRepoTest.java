package com.example.surveyapp.domain.product.domain.repository;

import com.example.surveyapp.config.testbase.DataJpaTestBase;
import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.domain.product.controller.dto.ProductCreateResponseDto;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.domain.model.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DisplayName("레포지터리: Product 레포지터리 테스트")
public class ProductRepoTest extends DataJpaTestBase {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("기능_테스트_상품 생성")
    public void 상품_생성() {
        // given
        Product product = Product.create("상품", 2500L, "설명", Status.ON_SALE);
        // when
        Product saved = productRepository.save(product);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isNotNull();
    }
}
