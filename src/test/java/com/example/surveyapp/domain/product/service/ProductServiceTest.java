package com.example.surveyapp.domain.product.service;

import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.domain.product.controller.dto.ProductCreateRequestDto;
import com.example.surveyapp.domain.product.controller.dto.ProductCreateResponseDto;
import com.example.surveyapp.domain.product.controller.dto.ProductResponseDto;
import com.example.surveyapp.domain.product.controller.dto.ProductUpdateRequestDto;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.domain.repository.ProductRepository;
import com.example.surveyapp.domain.product.service.dto.ProductUpdateResponseDto;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

@DisplayName("service: Product 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("기능_테스트_상품 생성을 관리자가 정상적으로 한다.")
    void 상품_생성() {
        // Given
        //테스트 전제 조건 및 환경 설정

        Product product = ProductFixtureGenerator.generateProductFixture();
        Long userId = 1L;

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus());

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        doNothing().when(userReader).validateUserRole(userId,UserRoleEnum.ADMIN);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        // When
        //실행할 행동
        ProductCreateResponseDto productCreateResponseDto = productService.createProduct(requestDto, userId);

        // Then
        //검증 사항
        verify(productRepository,times(1)).save(any(Product.class));
        assertEquals(product.getTitle(), productCreateResponseDto.getTitle());
        assertEquals(product.getPrice(),productCreateResponseDto.getPrice());
        assertEquals(product.getStatus(),productCreateResponseDto.getStatus());

    }

    @Test
    @DisplayName("예외_테스트_참여자는 상품생성이 불가능하다.")
    void 상품_생성_참여자는_불가능_하다() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Product product = ProductFixtureGenerator.generateProductFixture();
        Long userId = 1L;
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus());
        // When
        //실행할 행동

        doNothing().when(userReader).validateUserIdOrThrow(userId);
        doThrow(new CustomException(ErrorCode.NOT_ADMIN_USER_ERROR))
                .when(userReader)
                .validateUserRole(userId,UserRoleEnum.ADMIN);

        // Then
        //검증 사항
        assertThatThrownBy(() -> productService.createProduct(requestDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_ADMIN_USER_ERROR.getMessage());
    }

    @Test
    @DisplayName("예외_테스트_출제자는 상품생성이 불가능하다.")
    void 상품_생성_출제자는_불가능_하다() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Product product = ProductFixtureGenerator.generateProductFixture();
        Long userId = 1L;

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus());
        // When
        //실행할 행동
        doThrow(new CustomException(ErrorCode.NOT_ADMIN_USER_ERROR))
                .when(userReader)
                .validateUserRole(userId,UserRoleEnum.ADMIN);
        doNothing().when(userReader).validateUserIdOrThrow(userId);
        // Then
        //검증 사항
        assertThatThrownBy(() -> productService.createProduct(requestDto, userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_ADMIN_USER_ERROR.getMessage());
    }

    @Test
    @DisplayName("예외_테스트_상품 생성중 동일한 상품명을 가질 수 없다.")
    void 상품_생성시_동일한_상품명으로_생성_불가_하다() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Product product = ProductFixtureGenerator.generateProductFixture();
        Long userId = 1L;

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus());

        // When
        //실행할 행동
        doNothing().when(userReader).validateUserIdOrThrow(userId);
        doNothing().when(userReader).validateUserRole(userId,UserRoleEnum.ADMIN);
        when(productRepository.existsByTitleAndIsDeletedFalse(product.getTitle())).thenReturn(true);

        // Then
        //검증 사항
        assertThatThrownBy(() -> productService.createProduct(requestDto,userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_SAME_CREATE_PRODUCT_TITLE.getMessage());

    }

    @Test
    @DisplayName("기능_테스트_상품 전체를 조회한다.")
    void 전체_상품_조회() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Product product1 = ProductFixtureGenerator.generateProductFixture();
        Product product2 = ProductFixtureGenerator.generateProductFixture();
        List<Product> productList = List.of(product1, product2);

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        PageImpl<Product> products = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAllByStatusAndIsDeletedFalse(Status.ON_SALE,pageable)).thenReturn(products);
        // When
        //실행할 행동
        List<ProductResponseDto> productResponseDtos = productService.readAllProduct(page, size);

        // Then
        //검증 사항
        verify(productRepository,  times(1)).findAllByStatusAndIsDeletedFalse(Status.ON_SALE, pageable);
        assertThat(productResponseDtos.size()).isEqualTo(productList.size());


    }

    @Test
    @DisplayName("기능_테스트_상품 한개만 조회한다.")
    void 상품_단건_조회() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long id = 1L;
        Product product = ProductFixtureGenerator.generateProductFixture();

        when(productRepository.findByIdAndStatusAndIsDeletedFalse(id,Status.ON_SALE)).thenReturn(Optional.of(product));

        // When
        //실행할 행동
        ProductResponseDto product1 = productService.readOneProduct(id);

        // Then
        //검증 사항
        verify(productRepository, times(1)).findByIdAndStatusAndIsDeletedFalse(id,Status.ON_SALE);
        assertThat(product1.getTitle()).isEqualTo(product.getTitle());
    }

    @Test
    @DisplayName("기능_테스트_상품을 수정한다")
    void 상품_수정_가능하다() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long id = 1L;
        Product product = ProductFixtureGenerator.generateProductFixture();

        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto("변경된상품명", 2500L, "변경된설명", Status.ON_SALE);


        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.existsByTitleAndIsDeletedFalse("변경된상품명")).thenReturn(false);
        // When
        //실행할 행동
        ProductUpdateResponseDto responseDto = productService.updateProduct(id, requestDto);

        // Then
        //검증 사항
        verify(productRepository, times(1)).findById(id);
        assertThat(responseDto.getTitle()).isEqualTo("변경된상품명");
    }


    @Test
    @DisplayName("기능_테스트_상품을 삭제한다")
    void 상품_삭제() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Product product = ProductFixtureGenerator.generateProductFixture();

        when(productRepository.findByIdAndIsDeletedFalse(product.getId())).thenReturn(Optional.of(product));

        // When
        //실행할 행동
        productService.deleteProduct(product.getId(),userId);
        // Then
        //검증 사항
        assertThat(product.isDeleted()).isTrue();
        verify(productRepository,times(1)).findByIdAndIsDeletedFalse(product.getId());
    }
}