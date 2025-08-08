package com.example.surveyapp.domain.order.controller;

import com.example.surveyapp.config.generator.OrderFixtureGenerator;
import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.config.custommockuser.WithCustomMockUser;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.controller.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.OrderItem;
import com.example.surveyapp.domain.order.service.OrderService;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.user.domain.model.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName(" controller : Order 컨트롤러 테스트")
@WebMvcTest(controllers = OrderController.class,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class))
@AutoConfigureMockMvc(addFilters = false)
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
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user, "id" , 1L);
        Product product = ProductFixtureGenerator.generateProductFixture();
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);

        OrderItem orderItems = order.getOneOrderItemOrThrow();

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(product.getId());
        OrderCreateResponseDto responseDto = new OrderCreateResponseDto(order.getId(),
                order.getOrderNumber().getValue(),
                orderItems.getTitle(),
                reques,
                product.getPrice()
                );
        // When
        //실행할 행동
        when(orderService.createOrder(any(OrderCreateRequestDto.class), eq(user.getId()))).thenReturn(responseDto);

        ResultActions actions = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        //검증 사항
        verify(orderService, times(1)).createOrder(any(OrderCreateRequestDto.class),eq(user.getId()));
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(order.getTitle()))
                .andExpect(jsonPath("$.data.title").value(product.getTitle()))
                .andExpect(jsonPath("$.data.orderNumber").value(order.getOrderNumber()));


    }

    @Test
    @DisplayName("기능_테스트_관리자는 모든 계정의 주문 조회가 가능하다.")
    @WithCustomMockUser(id = 2, role = UserRoleEnum.ADMIN)
    void 관리자_주문_전체조회_가능하다() throws Exception{
        // Given
        //테스트 전제 조건 및 환경 설정
        List<OrderResponseDto> orderList = List.of(
                new OrderResponseDto(1L, "uuid1", 1L,"dohan1", "치킨",2500L,Status.ON_SALE, LocalDateTime.now()),
                new OrderResponseDto(2L, "uuid2", 2L,"dohan2", "피자",3500L,Status.ON_SALE, LocalDateTime.now())
                );

        when(orderService.readAllOrder(0,10)).thenReturn(orderList);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderList)));

        // Then
        //검증 사항
        verify(orderService,times(1)).readAllOrder(0,10);
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].username").value("dohan1"))
            .andExpect(jsonPath("$.data[1].username").value("dohan2"));

    }



    @Test
    @DisplayName("기능_테스트_참여자는 자신의 주문목록을 조회 할 수 있다.")
    @WithCustomMockUser(id = 3,role = UserRoleEnum.SURVEYEE)
    void 참여자_주문조회() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        User user = UserFixtureGenerator.generateUserFixture();
        List<OrderResponseDto> orderList = List.of(
                new OrderResponseDto(1L, "uuid1", 3L, "dohan1", "치킨", 2500L, Status.ON_SALE, LocalDateTime.now()),
                new OrderResponseDto(2L, "uuid2", 3L, "dohan1", "피자", 3500L, Status.ON_SALE, LocalDateTime.now()
                ));

        when(orderService.readMyOrderList(anyInt(),anyInt(),anyLong())).thenReturn(orderList);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderList)));

        // Then
        //검증 사항
        verify(orderService,times(1)).readMyOrderList(anyInt(),anyInt(),anyLong());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("dohan1"))
                .andExpect(jsonPath("$.data[1].username").value("dohan1"));

    }


    @Test
    @DisplayName("기능_테스트_주문자는 주문내역을 삭제할 수있다.")
    @WithCustomMockUser(id =3,role = UserRoleEnum.SURVEYEE)
    void 주문자_주문내역_삭제() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto orderResponseDto = new OrderResponseDto(1L, "uuid1", 3L, "dohan1", "치킨", 2500L, Status.ON_SALE, LocalDateTime.now());

        doNothing().when(orderService).deleteOrder(orderResponseDto.getOrderId(),orderResponseDto.getUserId());
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(delete("/api/orders/{id}",orderResponseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderResponseDto)));

        // Then
        //검증 사항
        verify(orderService, times(1)).deleteOrder(orderResponseDto.getOrderId(),
                orderResponseDto.getUserId());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("기능_테스트_관리자는 주문 단건을 조회할 수  있다.")
    @WithCustomMockUser(id = 1,role = UserRoleEnum.ADMIN)
    void 관리자_주문_단건_조회() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto orderResponseDto = new OrderResponseDto(1L, "uuid1", 3L, "dohan1", "치킨", 2500L, Status.ON_SALE, LocalDateTime.now());

        when(orderService.readOneOrder(orderResponseDto.getOrderId())).thenReturn(orderResponseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/{id}", orderResponseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderResponseDto)));

        // Then
        //검증 사항
        verify(orderService, times(1)).readOneOrder(orderResponseDto.getOrderId());

        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(orderResponseDto.getOrderNumber()));
    }

    @Test
    @DisplayName("기능_테스트_참여자는 자신의 주문 단건을 조회할 수 있다.")
    @WithCustomMockUser(id = 3,role = UserRoleEnum.SURVEYEE)
    void readOneMyOrder() throws Exception {
        // Given
        //테스트 전제 조건 및 환경 설정
        OrderResponseDto orderResponseDto = new OrderResponseDto(1L, "uuid1", 3L, "dohan1", "치킨", 2500L, Status.ON_SALE, LocalDateTime.now());

        when(orderService.readOneMyOrder(orderResponseDto.getOrderId(),orderResponseDto.getUserId())).thenReturn(orderResponseDto);
        // When
        //실행할 행동
        ResultActions actions = mockMvc.perform(get("/api/orders/my/{id}", orderResponseDto.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderResponseDto)));
        // Then
        //검증 사항
        verify(orderService, times(1)).readOneMyOrder(orderResponseDto.getOrderId(),orderResponseDto.getUserId());
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value(orderResponseDto.getOrderNumber()))
                .andExpect(jsonPath("$.data.userId").value(orderResponseDto.getUserId()));
    }
}
