package com.example.surveyapp.domain.product.controller;

import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.product.controller.dto.ProductCreateRequestDto;
import com.example.surveyapp.domain.product.controller.dto.ProductCreateResponseDto;
import com.example.surveyapp.domain.product.controller.dto.ProductResponseDto;
import com.example.surveyapp.domain.product.controller.dto.ProductUpdateRequestDto;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.service.ProductService;
import com.example.surveyapp.domain.product.service.dto.ProductUpdateResponseDto;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;


import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("서비스: Product 서비스 테스트")
@WebMvcTest(controllers = ProductController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
// 웹계층 테스트에 필요한 Bean들만 등록해주는 Annotations
// 당연히 데이터 접근 계층에 관련된 JPA와 연관된 Bean은 등록되질 않아야 하는데
// 왜 JPA 관련된, Audting 관련된 Bean 생성을 시도했을까
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;


    @Test
    @DisplayName("기능_테스트_상품을 생성한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품_생성한다() throws Exception {
        // Given

        Product product = ProductFixtureGenerator.generateProductFixture();
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice(),
                product.getStatus());
        ProductCreateResponseDto responseDto = new ProductCreateResponseDto(product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getStatus());

        when(productService.createProduct(any(ProductCreateRequestDto.class), eq(1L))).thenReturn(responseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService,times(1)).createProduct(any(ProductCreateRequestDto.class),eq(1L));

        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()));

    }

    @Test
    @DisplayName("기능_테스트_관리자 계정으로 상품을 전체 조회한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품_전체_조회를_한다() throws Exception {
        // Given
        ProductResponseDto product1 = new ProductResponseDto(1L, "상품1", 2000L, Status.ON_SALE);
        ProductResponseDto product2 = new ProductResponseDto(2L, "상품2", 2500L, Status.ON_SALE);
        List<ProductResponseDto> productList = List.of(product1, product2);

        when(productService.readAllProduct(0,10)).thenReturn(productList);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productList)));
        // Then
        //검증 사항

        verify(productService, times(1)).readAllProduct(0,10);

          actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("상품1"))
                .andExpect(jsonPath("$.data[1].title").value("상품2"));
    }

    @Test
    @DisplayName("기능_테스트_참여자 계정으로 상품 조회는 가능하다.")
    @WithCustomMockUser(id = 2, role = UserRoleEnum.SURVEYEE)
    void 상품_전체_조회_참여자계정은_가능해야한다() throws Exception {
        // Given
        ProductResponseDto product1 = new ProductResponseDto(1L, "상품1", 2000L, Status.ON_SALE);
        ProductResponseDto product2 = new ProductResponseDto(2L, "상품2", 2500L, Status.ON_SALE);
        List<ProductResponseDto> productList = List.of(product1, product2);

        when(productService.readAllProduct(0,10)).thenReturn(productList);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productList)));
        // Then
        //검증 사항

        verify(productService, times(1)).readAllProduct(0,10);

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("상품1"))
                .andExpect(jsonPath("$.data[1].title").value("상품2"));

    }

    @Test
    @DisplayName("기능_테스트_상품을 한개 조회한다.")
    @WithCustomMockUser(id = 2, role = UserRoleEnum.SURVEYEE)
    void 상품_한개를_조회한다() throws Exception{
        // Given
        //테스트 전제 조건 및 환경 설정
        Product product = ProductFixtureGenerator.generateProductFixture();
        ProductResponseDto productResponseDto = new ProductResponseDto(product.getId(), product.getTitle(), product.getPrice(), product.getStatus());
        when(productService.readOneProduct(product.getId())).thenReturn(productResponseDto);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/products/{id}",product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productResponseDto)));

        // Then
        //검증 사항
        verify(productService, times(1)).readOneProduct(product.getId());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("상품명"));

    }

    @Test
    @DisplayName("기능_테스트_상품을 수정 한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품을_수정한다() throws Exception{
        // Given
        //테스트 전제 조건 및 환경 설정
        Long productId = 1L;
        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto("변경된 상품명", 2500L, "변경된 상품설명", Status.ON_SALE);
        ProductUpdateResponseDto responseDto = new ProductUpdateResponseDto(1L, "변경된 상품명", "변경된 상품설명:", 2500L, Status.ON_SALE);

        when(productService.updateProduct(eq(productId), any(ProductUpdateRequestDto.class))).thenReturn(responseDto);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(patch("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService,times(1))
                .updateProduct(eq(productId), any(ProductUpdateRequestDto.class));
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("변경된 상품명"));
    }

    @Test
    @DisplayName("기능_테스트_상품을 삭제 한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품을_삭제한다() throws Exception{

        // Given
        //테스트 전제 조건 및 환경 설정
        Long productId = 1L;
        Long userId = 1L;

        ProductCreateRequestDto requestDto = new ProductCreateRequestDto("상품명", "상품설명", 2500L, Status.ON_SALE);

        doNothing().when(productService).deleteProduct(productId,userId);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(delete("/api/products/{id}", productId)
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));


        // Then
        //검증 사항
        verify(productService, times(1)).deleteProduct(productId,userId);
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());


    }
}