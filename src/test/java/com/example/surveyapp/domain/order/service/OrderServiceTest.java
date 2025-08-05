package com.example.surveyapp.domain.order.service;

import com.example.surveyapp.config.generator.OrderFixtureGenerator;
import com.example.surveyapp.config.generator.ProductFixtureGenerator;
import com.example.surveyapp.config.generator.UserFixtureGenerator;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.controller.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.repository.OrderRepository;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.point.service.PointService;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.Status;
import com.example.surveyapp.domain.product.domain.model.repository.ProductRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
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
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PointService pointService;

    @Test
    @DisplayName("기능_테스트_참여자가 주문을 생성한다")
    void 주문_생성() {

        // Given
        //테스트 전제 조건 및 환경 설정
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user, "id" , 1L);
        Product product = ProductFixtureGenerator.generateProductFixture();
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);
        Point point = Point.of(user);
        point.pointCharge(5000L);


        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(product.getId()) ;
        // When
        //실행할 행동
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pointRepository.findByUser(user)).thenReturn(Optional.of(point));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(pointService).redeem(anyLong(), anyLong(),anyLong());
        OrderCreateResponseDto responseDto = orderService.createOrder(requestDto, user.getId());

        // Then
        //검증 사항
        assertThat(responseDto.getPrice() >= product.getPrice());
        assertThat(responseDto.getPrice()).isEqualTo(product.getPrice());
        assertThat(responseDto.getStatus()).isEqualTo(Status.ON_SALE);
        assertThat(responseDto.getTitle()).isEqualTo(product.getTitle());

        verify(pointService, times(1)).redeem(eq(user.getId()),eq(product.getPrice()), eq(order.getId()));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("기능_테스트_관리자가 주문을 조회한다.")
    void 관리자_주문_조회() {
        // Given
        //테스트 전제 조건 및 환경 설정
        User admin = User.of("TES4@test.com", "test1123~", "김도한", "ADMIN", UserRoleEnum.ADMIN);
        User user1 = User.of("test1@test.com", "test1234!", "김민진", "민진1", UserRoleEnum.SURVEYEE);
        User user2 = User.of("test2@test.com", "test1234!", "김민성", "민성1", UserRoleEnum.SURVEYEE);
        User user3 = User.of("test3@test.com", "test1234!", "김도한", "도한1", UserRoleEnum.SURVEYEE);

        Product product = ProductFixtureGenerator.generateProductFixture();
        Product product1 = ProductFixtureGenerator.generateProductFixture();
        Product product2 = ProductFixtureGenerator.generateProductFixture();
        Order order1 = OrderFixtureGenerator.generateOrderFixture(user1,product);
        Order order2 = OrderFixtureGenerator.generateOrderFixture(user2,product1);
        Order order3 = OrderFixtureGenerator.generateOrderFixture(user3,product2);
        List<Order> orderList = List.of(order1, order2, order3);

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        PageImpl<Order> orders = new PageImpl<>(orderList, pageable, orderList.size());

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orders);
        // When
        //실행할 행동
        List<OrderResponseDto> orderResponseDtos = orderService.readAllOrder(page, size);

        // Then
        //검증 사항
        assertThat(orderResponseDtos.size()).isEqualTo(orderResponseDtos.size());
        assertThat(admin.getUserRole()).isEqualTo(UserRoleEnum.ADMIN);
        assertThat(order1.getUser()).isEqualTo(user1);
        assertThat(order2.getUser()).isEqualTo(user2);
        assertThat(order3.getUser()).isEqualTo(user3);

    }

    @Test
    @DisplayName("기능_테스트_관리자가 주문 단건을 조회한다.")
    void 관리자_주문_단건_조회하기() {
        // Given
        //테스트 전제 조건 및 환경 설정
        User user = UserFixtureGenerator.generateUserFixture();
        Product product = ProductFixtureGenerator.generateProductFixture();
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);

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
        User user1 = User.of("test1@test.com", "test1234!", "김민진", "민진1", UserRoleEnum.SURVEYEE);
        Product product = ProductFixtureGenerator.generateProductFixture();
        Product product1 = ProductFixtureGenerator.generateProductFixture();
        Product product2 = ProductFixtureGenerator.generateProductFixture();
        Order order1 = OrderFixtureGenerator.generateOrderFixture(user1,product);
        Order order2 = OrderFixtureGenerator.generateOrderFixture(user1,product1);
        Order order3 = OrderFixtureGenerator.generateOrderFixture(user1,product2);
        List<Order> orderList1 = List.of(order1, order2, order3);

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        PageImpl<Order> orders1 = new PageImpl<>(orderList1, pageable, orderList1.size());
        when(orderRepository.findByUser(user1, pageable)).thenReturn(orders1);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        // When
        List<OrderResponseDto> response1 = orderService.readMyOrderList(page, size, user1.getId());

        // Then
        assertThat(response1.size()).isEqualTo(orderList1.size());

        //주문과 오더 id 일치하는지 검증
        for (int i = 0; i < orderList1.size(); i++) {
            Order order = orderList1.get(i);
            OrderResponseDto orderResponseDto = response1.get(i);

            assertThat(orderResponseDto.getOrderId()).isEqualTo(order.getId());
        }

    }
    @Test
    @DisplayName("기능_테스트_참여자는 주문 단건 조회를 할 수 있다.")
    void 참여자_주문_단건_조회() {
        // Given
        //테스트 전제 조건 및 환경 설정
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user, "id" , 1L);
        Product product = ProductFixtureGenerator.generateProductFixture();
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        // When
        //실행할 행동
        OrderResponseDto responseDto = orderService.readOneMyOrder(order.getId(), order.getUser().getId());

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
        User user = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(user,"id",1L);
        User anotherUser = UserFixtureGenerator.generateUserFixture();
        ReflectionTestUtils.setField(anotherUser,"id",2L);
        Product product = ProductFixtureGenerator.generateProductFixture();
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.of(anotherUser));

        // When
        //실행할 행동

        // Then
        //검증 사항
        assertThatThrownBy(() -> orderService.readOneMyOrder(order.getId(),anotherUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("본인 주문만 확인 할 수 있습니다.");
    }

    @Test
    @DisplayName("예외_테스트_주문 생성 시 금액이 부족하면 구매할 수 없다.")
    void 참여자_주문_생성_시_포인트가_부족하면_교환할_수_없다(){
        // Given
        //테스트 전제 조건 및 환경 설정
        User user = UserFixtureGenerator.generateUserFixture();
        Point point = Point.of(user);
        point.pointCharge(5000L);

        ReflectionTestUtils.setField(user,"id",1L);
        Product product = ProductFixtureGenerator.generateProductFixture();
        ReflectionTestUtils.setField(product,"price",5500L);
        Order order = OrderFixtureGenerator.generateOrderFixture(user,product);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(pointRepository.findByUser(user)).thenReturn(Optional.of(point));

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(product.getId());
        // When
        //실행할 행동

        // Then
        //검증 사항
        assertThatThrownBy(() -> orderService.createOrder(requestDto,user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("포인트가 부족합니다.");
    }
}