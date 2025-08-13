package com.example.surveyapp.domain.order.presentation;

import com.example.surveyapp.config.generator.OrderFixtureGenerator;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.application.OrderService;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName(" controller : Order 컨트롤러 테스트")
@WebMvcTest(controllers = OrderController.class,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class OrderControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("기능_테스트_참여자가 주문을 생성한다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.SURVEYEE)
    void 주문_생성() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Order order = OrderFixtureGenerator.generateOrderFixture(userId);
        ReflectionTestUtils.setField(order, "id", 1L);
        OrderItem item = order.getOneOrderItemOrThrow();

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(item.getProductId());
        OrderCreateResponseDto responseDto = new OrderCreateResponseDto(1L,
                order.getOrderNumber().getValue(),
                item.getTitle(),
                Status.ON_SALE.getStatus(),
                order.orderAmount()
        );
        // When
        //실행할 행동
        when(orderService.createOrder(any(OrderCreateRequestDto.class), eq(userId))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(orderService, times(1))
                .createOrder(any(OrderCreateRequestDto.class), eq(userId));
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").isString())
                .andExpect(jsonPath("$.data.title").value(item.getTitle()))
                .andDo(document("POST-201-주문-생성-API",
//                        requestHeaders(
//                                headerWithName("Authorization")
//                                        .description("JWT 인증 토큰 (Bearer + 토큰값)")
//                                        .attributes(key("format").value("Bearer {jwt_token}")),
//                                headerWithName("Accept").description("응답 데이터 타입")
//                        ),
//                        responseHeaders(
//                                headerWithName(LOCATION).description("생성된 주문 위치")
//                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data.orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("주문 상품 제목"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description("주문 상품 상태"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("주문 상품 금액"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("주문 생성일"))));

    }

    @Test
    @DisplayName("기능_테스트_관리자는 모든 계정의 주문 조회가 가능하다.")
    @WithCustomMockUser(id = 2, role = UserRoleEnum.ADMIN)
    void 관리자_주문_전체조회_가능하다() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        List<OrderResponseDto> orderList = List.of(
                new OrderResponseDto(1L, "uuid1", 1L, "dohan1", 1L, "chicken", 2500L, Status.ON_SALE.getStatus(), LocalDateTime.now()),
                new OrderResponseDto(2L, "uuid1", 3L, "dohan2", 2L, "pizza", 3500L, Status.ON_SALE.getStatus(), LocalDateTime.now())
        );

        when(orderService.readAllOrder(0, 10)).thenReturn(orderList);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderList)));

        // Then
        //검증 사항
        verify(orderService, times(1)).readAllOrder(0, 10);
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("dohan1"))
                .andExpect(jsonPath("$.data[1].username").value("dohan2"))
                .andDo(document("GET-200-관리자-전체-주문-조회-API",
//                       responseHeaders(
//                                headerWithName(LOCATION).description("관리자 전체 주문 조회 위치")
//                        ),
//                        // /api/board?page=1&size=10
                        queryParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("data[].orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data[].orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("주문자 ID"),
                                fieldWithPath("data[].username").type(JsonFieldType.STRING).description("주문자 이름"),
                                fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("주문 상품 제목"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description(" 상품 상태"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("주문 상품 금액"),
                                fieldWithPath("data[].createAt").type(JsonFieldType.STRING).description("주문 생성일"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시각"))));


    }

    @Test
    @DisplayName("기능_테스트_참여자는 자신의 주문목록을 조회 할 수 있다.")
    @WithCustomMockUser(id = 3, role = UserRoleEnum.SURVEYEE)
    void 참여자_주문_목록_조회() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        List<OrderResponseDto> orderList = List.of(
                new OrderResponseDto(1L, "uuid1", 3L, "dohan1", 1L, "chicken", 2500L, Status.ON_SALE.getStatus(), LocalDateTime.now()),
                new OrderResponseDto(2L, "uuid1", 3L, "dohan1", 1L, "pizza", 3500L, Status.ON_SALE.getStatus(), LocalDateTime.now())
        );

        when(orderService.readMyOrderList(0, 10, 3L)).thenReturn(orderList);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderList)));

        // Then
        //검증 사항
        verify(orderService, times(1)).readMyOrderList(anyInt(), anyInt(), anyLong());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("dohan1"))
                .andExpect(jsonPath("$.data[1].username").value("dohan1"))
                .andDo(document("GET-200-참여자-본인-주문-목록-조회-API",
//                        responseHeaders(
//                                headerWithName(LOCATION).description("관리자 전체 주문 조회 위치")
//                        ),
                        // /api/board?page=1&size=10
                        queryParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("data[].orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data[].orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("주문자 ID"),
                                fieldWithPath("data[].username").type(JsonFieldType.STRING).description("주문자 이름"),
                                fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("주문 상품 제목"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description(" 상품 상태"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("주문 상품 금액"),
                                fieldWithPath("data[].createAt").type(JsonFieldType.STRING).description("주문 생성일"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시각"))));

    }


    @Test
    @DisplayName("기능_테스트_주문자는 주문내역을 삭제할 수있다.")
    @WithCustomMockUser(id = 3, role = UserRoleEnum.SURVEYEE)
    void 주문자_주문내역_삭제() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto responseDto = new OrderResponseDto(1L, "uuid1", 3L, "dohan1", 1L, "chicken", 2500L, Status.ON_SALE.getStatus(), LocalDateTime.now());

        doNothing().when(orderService).deleteOrder(responseDto.getOrderId(), responseDto.getUserId());
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(delete("/api/orders/{id}", responseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseDto)));

        // Then
        //검증 사항
        verify(orderService, times(1)).deleteOrder(responseDto.getOrderId(),
                responseDto.getUserId());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(document("DEL-200-참여자본인-주문-내역-삭제",
                        pathParameters(
                                parameterWithName("id").description("주문 ID")
                        )
//                        ,
//                        responseHeaders(
//                                headerWithName(LOCATION).description("삭제된 주문 위치")
//                        )
                ));

    }

    @Test
    @DisplayName("기능_테스트_관리자는 주문 단건을 조회할 수  있다.")
    @WithCustomMockUser(id = 1, role = UserRoleEnum.ADMIN)
    void 관리자_주문_단건_조회() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto responseDto = new OrderResponseDto(1L, "uuid1", 1L, "dohan1", 1L, "chicken", 2500L, Status.ON_SALE.getStatus(), LocalDateTime.now());

        when(orderService.readOneOrder(responseDto.getOrderId())).thenReturn(responseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/{id}", responseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseDto)));

        // Then
        //검증 사항
        verify(orderService, times(1)).readOneOrder(responseDto.getOrderId());

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(responseDto.getOrderNumber()))
                .andDo(document("GET-200-관리자-주문-단건-조회-API",
//                        responseHeaders(
//                                headerWithName(LOCATION).description("관리자 전체 주문 조회 위치")
//                        ),
                        // /api/board/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data.orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("주문자 ID"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("주문자 이름"),
                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("주문 상품 제목"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description(" 상품 상태"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("주문 상품 금액"),
                                fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("주문 생성일"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시각"))));

    }

    @Test
    @DisplayName("기능_테스트_참여자는 자신의 주문 단건을 조회할 수 있다.")
    @WithCustomMockUser(id = 3, role = UserRoleEnum.SURVEYEE)
    void 참여자_본인_주문_단건_조회() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto responseDto = new OrderResponseDto(1L, "uuid1", 3L, "dohan1", 1L, "chicken", 2500L, Status.ON_SALE.getStatus(), LocalDateTime.now());

        when(orderService.readOneMyOrder(responseDto.getOrderId(), responseDto.getUserId())).thenReturn(responseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/my/{id}", responseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseDto)));
        // Then
        //검증 사항
        verify(orderService, times(1)).readOneMyOrder(responseDto.getOrderId(), responseDto.getUserId());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(responseDto.getOrderNumber()))
                .andExpect(jsonPath("$.data.userId").value(responseDto.getUserId()))
                .andDo(document("GET-200-참여자-본인-주문-단건-조회-API",
//                        responseHeaders(
//                                headerWithName(LOCATION).description("관리자 전체 주문 조회 위치")
//                        ),
                        // /api/board/{id}
                        pathParameters(
                                parameterWithName("id").description("주문 ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("data.orderNumber").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("주문자 ID"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("주문자 이름"),
                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("주문 상품 제목"),
                                fieldWithPath("data.status").type(JsonFieldType.STRING).description(" 상품 상태"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("주문 상품 금액"),
                                fieldWithPath("data.createAt").type(JsonFieldType.STRING).description("주문 생성일"),
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시각"))));

    }
}
