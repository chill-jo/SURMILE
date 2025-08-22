package com.example.surveyapp.domain.order.application;

import com.example.surveyapp.config.generator.OrderFixtureGenerator;
import com.example.surveyapp.domain.order.application.mapper.OrderResponseMapper;
import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.repository.OrderOutboxRepository;
import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.application.facade.ProductFacade;
import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.repository.OrderRepository;
import com.example.surveyapp.domain.product.presentation.dto.ProductInfoDto;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.global.reader.UserReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private OrderResponseMapper orderResponseMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OrderOutboxRepository orderOutboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("기능_테스트_참여자가 주문을 생성한다")
    void 주문_생성() throws Exception {

        // Given
        //테스트 전제 조건 및 환경 설정
        Long userId = 1L;
        Long productId = 1L;
        ProductInfoDto productInfoDto = new ProductInfoDto("chicken", 2500L, Status.ON_SALE);
        Order order = OrderFixtureGenerator.generateOrderFixture(userId);
        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(productId);

        // When
        //실행할 행동
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productFacade.findProductInfo(productId)).thenReturn(productInfoDto);
        doNothing().when(userReader).validateUserIdOrThrow(userId);
        when(orderOutboxRepository.save(any(OrderOutbox.class))).thenReturn(mock(OrderOutbox.class));
        when(objectMapper.writeValueAsString(any(OrderCreateEvent.class))).thenReturn("json-payload");

        OrderCreateResponseDto responseDto = orderService.createOrder(requestDto, userId);

        // Then
        //검증 사항
        assertThat(responseDto.getPrice() >= order.orderAmount());
        assertThat(responseDto.getOrderNumber()).isNotBlank();
        assertThat(responseDto.getStatus()).isEqualTo(Status.ON_SALE.getStatus());
        assertThat(responseDto.getTitle()).isEqualTo(order.getOrderItem().getTitle());

        verify(eventPublisher).publishEvent(any(OrderCreateEvent.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderOutboxRepository, times(1)).save(any(OrderOutbox.class));

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
        when(orderResponseMapper.toDto(order1)).thenReturn(OrderResponseDto.from(
                order1,"dohan1",Status.ON_SALE.getStatus()));
        when(orderResponseMapper.toDto(order2)).thenReturn(OrderResponseDto.from(
                order2,"dohan2",Status.ON_SALE.getStatus()));
        when(orderResponseMapper.toDto(order3)).thenReturn(OrderResponseDto.from(
                order3,"dohan3",Status.ON_SALE.getStatus()));
        // When
        //실행할 행동
        Page<OrderResponseDto> orderResponseDtos = orderService.readAllOrder(page, size);

        // Then
        //검증 사항
        assertThat(orderResponseDtos.getContent().size()).isEqualTo(orderList.size());
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

        when(orderRepository.findByIdAndIsDeletedFalse(order.getId())).thenReturn(Optional.of(order));
        when(orderResponseMapper.toDto(order)).thenReturn(OrderResponseDto.from(order,any(),Status.ON_SALE.getStatus()));
        // When
        //실행할 행동
        OrderResponseDto orderResponseDto = orderService.readOneOrder(order.getId());

        // Then
        //검증 사항
        verify(orderRepository).findByIdAndIsDeletedFalse(order.getId());
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
        ReflectionTestUtils.setField(order,"id",1L);
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        PageImpl<Order> orders = new PageImpl<>(List.of(order), pageable, 1);
        doNothing().when(userReader).validateUserIdOrThrow(userId);

        when(orderRepository.findByUserIdAndIsDeletedFalse(userId, pageable)).thenReturn(orders);
        when(orderResponseMapper.toDto(order)).thenReturn(OrderResponseDto.from(
                order,"dohan1",Status.ON_SALE.getStatus()));

        // When
        Page<OrderResponseDto> responseDto = orderService.readMyOrderList(page, size, userId);

        // Then
        assertThat(responseDto.getContent().stream().map(OrderResponseDto::getOrderId)
                .toList()).isEqualTo(List.of(1L));
    }

@Test
@DisplayName("기능_테스트_참여자는 주문 단건 조회를 할 수 있다.")
void 참여자_주문_단건_조회() {
    // Given
    //테스트 전제 조건 및 환경 설정
    Long userId = 1L;
    Order order = OrderFixtureGenerator.generateOrderFixture(userId);

    when(orderRepository.findByIdAndIsDeletedFalse(order.getId())).thenReturn(Optional.of(order));
    when(orderResponseMapper.toDto(order)).thenReturn(OrderResponseDto.from(order,any(),Status.ON_SALE.getStatus()));

    // When
    //실행할 행동
    OrderResponseDto responseDto = orderService.readOneMyOrder(order.getId(), order.getUserId());

    // Then
    //검증 사항
    verify(orderRepository).findByIdAndIsDeletedFalse(order.getId());

    assertThat(responseDto.getOrderNumber()).isEqualTo(order.getOrderNumber().getValue());

}

@Test
@DisplayName("예외_테스트_참여자는 자신이 주문하지 않은 주문은 볼수 없다.")
void 자신이_주문하지않은_다른_주문은_조회가_불가능하다() {
    // Given
    //테스트 전제 조건 및 환경 설정
    Long ownerId= 2L; //주문자 ID
    Long userId = 1L; //검색자 ID
    Order order = OrderFixtureGenerator.generateOrderFixture(ownerId);
    ReflectionTestUtils.setField(order,"id",1L);


    when(orderRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(order));
    doNothing().when(userReader).validateUserIdOrThrow(userId);

    // When
    //실행할 행동

    // Then
    //검증 사항
    assertThatThrownBy(() -> orderService.readOneMyOrder(1L,userId))
            .isInstanceOf(OrderException.class)
            .hasMessageContaining(OrderErrorCode.NOT_YOUR_ORDER.getMessage());
}
}