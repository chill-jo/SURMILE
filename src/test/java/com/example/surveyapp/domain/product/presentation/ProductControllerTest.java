package com.example.surveyapp.domain.product.presentation;

import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.config.testbase.WebMvcTestBase;
import com.example.surveyapp.config.testmockbeans.TestMockBeans;
import com.example.surveyapp.domain.product.presentation.dto.*;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.application.ProductService;
import com.example.surveyapp.domain.product.application.dto.ProductUpdateResponseDto;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;


import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("controller: Product 컨트롤 테스트")
@Import(TestMockBeans.class)
class ProductControllerTest extends WebMvcTestBase {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("기능_테스트_상품을 생성한다")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품_생성한다() throws Exception {
        // Given

        Product product = ProductFixtureGenerator.generateProductFixture();
        ReflectionTestUtils.setField(product,"id",1L);
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto(product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus());
        ProductCreateResponseDto responseDto = new ProductCreateResponseDto(product.getId(),
                product.getTitle(),
                product.getContent(),
                product.getPrice().getValue(),
                product.getStatus().getStatus());

        when(productService.createProduct(any(ProductCreateRequestDto.class), eq(1L))).thenReturn(responseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(post("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService,times(1)).createProduct(any(ProductCreateRequestDto.class),eq(1L));

        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(requestDto.getTitle()))
                .andDo(document("product/create-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 상품 위치")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("상품 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상품 상태")

                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                                )));

    }

    @Test
    @DisplayName("기능_테스트_관리자 계정으로 상품을 전체 조회한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품_전체_조회를_한다() throws Exception {
        // Given
        ProductResponseDto product1 = new ProductResponseDto(1L, "상품1", 2000L, Status.ON_SALE.getStatus());
        ProductResponseDto product2 = new ProductResponseDto(2L, "상품2", 2500L, Status.ON_SALE.getStatus());
        List<ProductResponseDto> productList = List.of(product1, product2);

        when(productService.readAllProduct(0,10)).thenReturn(productList);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .param("page", "0")
                        .param("size", "10")
                       );
        // Then
        //검증 사항

        verify(productService, atLeastOnce()).readAllProduct(0,10);

          actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("상품1"))
                .andExpect(jsonPath("$.data[1].title").value("상품2"))
                  .andDo(document("product/get-products",
                          requestHeaders(
                                  headerWithName(HttpHeaders.AUTHORIZATION)
                                          .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                          .attributes(key("format").value("Bearer {jwt_token"))
                          ),
                          // /api/products?page=1&size=10
                          queryParameters(
                                  parameterWithName("page").description("페이지 번호").optional(),
                                  parameterWithName("size").description("페이지 크기").optional()
                          ),
                          responseFields(
                                  fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                  fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                  fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                  fieldWithPath("data[].title").type(JsonFieldType.STRING).description("상품 제목"),
                                  fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                  fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상품 상태"),
                                  fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                          )));

    }

    @Test
    @DisplayName("기능_테스트_참여자 계정으로 상품 조회는 가능하다.")
    @WithCustomMockUser(id = 2, role = UserRoleEnum.SURVEYEE)
    void 상품_전체_조회_참여자계정은_가능해야한다() throws Exception {
        // Given
        ProductResponseDto product1 = new ProductResponseDto(1L, "상품1", 2000L, Status.ON_SALE.getStatus());
        ProductResponseDto product2 = new ProductResponseDto(2L, "상품2", 2500L, Status.ON_SALE.getStatus());
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

        actions
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
        ReflectionTestUtils.setField(product,"id",1L);
        ProductResponseDto productResponseDto = new ProductResponseDto(product.getId(), product.getTitle(), product.getPrice().getValue(), product.getStatus().getStatus());
        when(productService.readOneProduct(product.getId())).thenReturn(productResponseDto);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/products/{id}",product.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productResponseDto)));

        // Then
        //검증 사항
        verify(productService, times(1)).readOneProduct(product.getId());
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("상품명"))
                .andDo(document("product/get-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        // /api/orders/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 제목"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )));

    }

    @Test
    @DisplayName("기능_테스트_상품을 수정 한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품을_수정한다() throws Exception{
        // Given
        //테스트 전제 조건 및 환경 설정
        Long productId = 1L;
        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto("변경된 상품명", 2500L, "변경된 상품설명", Status.ON_SALE);
        ProductUpdateResponseDto responseDto = new ProductUpdateResponseDto(1L, "변경된 상품명", "변경된 상품설명:", 2500L, Status.ON_SALE.getStatus());

        when(productService.updateProduct(eq(productId), any(ProductUpdateRequestDto.class))).thenReturn(responseDto);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(patch("/api/products/{id}", productId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService,times(1))
                .updateProduct(eq(productId), any(ProductUpdateRequestDto.class));
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("변경된 상품명"))
                .andDo(document("product/update-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        // /api/products/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("상품 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상품 상태")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("변경 된 상품 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("변경 된 상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("변경 된 상품 가격"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("변경 된 상품 상태"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")

                        )));
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                        .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService, times(1)).deleteProduct(productId,userId);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(document("product/delete-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        // /api/products/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data").description("null"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )));
    }

    @Test
    @DisplayName("기능_테스트_상품 상태를 변경 한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 상품_상태를_변경한다() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Product product = ProductFixtureGenerator.generateProductFixture();
        ReflectionTestUtils.setField(product,"id",1L);

        ProductStatusUpdateRequestDto requestDto = new ProductStatusUpdateRequestDto(Status.ON_SALE);
        ProductStatusUpdateResponseDto responseDto = new ProductStatusUpdateResponseDto(Status.STOPPED_SALE.getStatus());
        when(productService.statusUpdate(eq(userId), eq(product.getId()),any(ProductStatusUpdateRequestDto.class))).thenReturn(responseDto);

        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(patch("/api/products/{id}/status", product.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer {jwt_token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(productService, times(1)).statusUpdate(eq(userId),eq(product.getId()),any(ProductStatusUpdateRequestDto.class));

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newStatus").value(Status.STOPPED_SALE.getStatus()))
                .andDo(document("product/update-status-product",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("JWT 인증 토큰 (Bearer + 토큰 값")
                                        .attributes(key("format").value("Bearer {jwt_token"))
                        ),
                        // /api/products/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")),
                        requestFields(
                                fieldWithPath("newStatus").type(JsonFieldType.STRING).description("상품 상태")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("요청 결과 메시지"),
                                fieldWithPath("data.newStatus").type(JsonFieldType.STRING).description("변경 된 상품 상태"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프")
                        )));
    }

}