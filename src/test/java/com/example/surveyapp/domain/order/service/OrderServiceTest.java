package com.example.surveyapp.domain.order.service;

import com.example.surveyapp.config.generator.OrderFixtureGenerator;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.controller.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.facade.ProductFacade;
import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.repository.OrderRepository;
import com.example.surveyapp.domain.product.controller.dto.ProductInfoDto;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("service : Order 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserReader userReader;

    @Mock
    private ProductFacade productFacade;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("기능_테스트_참여자가 주문을 생성한다")
    void 주문_생성() {

        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Long productId = 1L;
        ProductInfoDto productInfoDto = new ProductInfoDto("chicken", 2500L, Status.ON_SALE, "ON_SALE");
        Order order = OrderFixtureGenerator.generateOrderFixture(userId);
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(productId);
        // When
        //실행할 행동
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productFacade.findProductInfo(productId)).thenReturn(productInfoDto);
        doNothing().when(userReader).validateUserIdOrThrow(userId);
        doNothing().when(eventPublisher).publishEvent(any());

        OrderCreateResponseDto responseDto = orderService.createOrder(requestDto, userId);

        // Then
        //검증 사항
        assertThat(responseDto.getPrice() >= order.orderAmount());
        assertThat(responseDto.getOrderNumber()).isNotBlank();
        assertThat(responseDto.getStatus()).isEqualTo("ON_SALE");
        assertThat(responseDto.getTitle()).isEqualTo(order.getOneOrderItemOrThrow().getTitle());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("기능_테스트_관리자가 주문을 조회한다.")
    void 관리자_주문_조회() {
        // Given
        //테스트 전제 조건 및 환경 설정

        Order order1 = OrderFixtureGenerator.generateOrderFixture(1L);
        Order order2 = OrderFixtureGenerator.generateOrderFixture(2L);
        Order order3 = OrderFixtureGenerator.generateOrderFixture(3L);
        List<Order> orderList = List.of(order1, order2, order3);

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        PageImpl<Order> orders = new PageImpl<>(orderList, pageable, orderList.size());

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orders);
        when(userReader.usernameById(1L)).thenReturn("dohan1");
        when(userReader.usernameById(2L)).thenReturn("dohan2");
        when(userReader.usernameById(3L)).thenReturn("dohan3");
        // When
        //실행할 행동
        List<OrderResponseDto> orderResponseDtos = orderService.readAllOrder(page, size);

        // Then
        //검증 사항
        assertThat(orderResponseDtos.size()).isEqualTo(orderResponseDtos.size());
        assertThat(order1.getUserId()).isEqualTo(1L);
        assertThat(order2.getUserId()).isEqualTo(2L);
        assertThat(order3.getUserId()).isEqualTo(3L);

    }

    @Test
    @DisplayName("기능_테스트_관리자가 주문 단건을 조회한다.")
    void 관리자_주문_단건_조회하기() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Long productId = 1L;

        Order order = OrderFixtureGenerator.generateOrderFixture(userId);
        ReflectionTestUtils.setField(order, "id", 1L);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        // When
        //실행할 행동
        OrderResponseDto orderResponseDto = orderService.readOneOrder(order.getId());

        // Then
        //검증 사항
        verify(orderRepository).findById(order.getId());
        assertThat(orderResponseDto.getOrderId()).isEqualTo(orderResponseDto.getOrderId());
        assertThat(orderResponseDto.getOrderNumber()).isEqualTo(orderResponseDto.getOrderNumber());

    }

    @Test
    @DisplayName("기능_테스트_본인의 주문 이력은 본인만 확인 할 수 있다.")
    void 본인_주문내역_확인하기() {
        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Order order = OrderFixtureGenerator.generateOrderFixture(userId);
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        PageImpl<Order> orders = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findByUserId(userId, pageable)).thenReturn(orders);
        doNothing().when(userReader).validateUserIdOrThrow(userId);

        // When
        List<OrderResponseDto> responseDto = orderService.readMyOrderList(page, size, userId);

        // Then
        assertThat(responseDto.stream().map(OrderResponseDto::getOrderId)
                .toList()).isEqualTo(1L);
    }

@Test
@DisplayName("기능_테스트_참여자는 주문 단건 조회를 할 수 있다.")
void 참여자_주문_단건_조회() {
    // Given
    //테스트 전제 조건 및 환경 설정
    Long userId = 1L;
    Order order = OrderFixtureGenerator.generateOrderFixture(userId);

    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    doNothing().when(userReader).validateUserIdOrThrow(userId);
    // When
    //실행할 행동
    OrderResponseDto responseDto = orderService.readOneMyOrder(order.getId(), order.getUserId());

    // Then
    //검증 사항
    verify(orderRepository).findById(responseDto.getOrderId());

    assertThat(responseDto.getOrderNumber()).isEqualTo(responseDto.getOrderNumber());

}

@Test
@DisplayName("예외_테스트_참여자는 자신이 주문하지 않은 주문은 볼수 없다.")
void 자신이_주문하지않은_다른_주문은_조회가_불가능하다() {
    // Given
    //테스트 전제 조건 및 환경 설정
    Long userId = 1L;
    Order order = OrderFixtureGenerator.generateOrderFixture(userId);

    when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
    doNothing().when(userReader).validateUserIdOrThrow(userId);

    // When
    //실행할 행동

    // Then
    //검증 사항
    assertThatThrownBy(() -> orderService.readOneMyOrder(order.getId(),userId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(ErrorCode.NOT_YOUR_ORDER.getMessage());
}
}