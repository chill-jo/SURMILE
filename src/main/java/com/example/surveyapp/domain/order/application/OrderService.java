package com.example.surveyapp.domain.order.application;

import com.example.surveyapp.domain.order.exception.OrderErrorCode;
import com.example.surveyapp.domain.order.exception.OrderException;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.domain.event.OrderCreateEvent;
import com.example.surveyapp.domain.order.application.facade.ProductFacade;
import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItemPoints;
import com.example.surveyapp.domain.order.domain.repository.OrderRepository;;
import com.example.surveyapp.domain.product.presentation.dto.ProductInfoDto;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserReader userReader;
    private final ProductFacade productFacade;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto requestDto, Long userId) {
        userReader.validateUserIdOrThrow(userId);

        ProductInfoDto product = productFacade.findProductInfo(requestDto.getProductId());

        OrderItem item = OrderItem.of(requestDto.getProductId(),
                product.getTitle(),
                OrderItemPoints.of(product.getPrice()));

        Order order = Order.of(
                userId,
                List.of(item)
                );

        String status = product.getStatus().getStatus();

        Order saveOrder = orderRepository.save(order);
        //이벤트 발행
        eventPublisher.publishEvent(new OrderCreateEvent(order.getId(),
                userId,
                order.orderAmount()));

       return OrderCreateResponseDto.from(saveOrder,status);
    }

    public List<OrderResponseDto> readAllOrder(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAll(pageable);
        List<Order> ordersList = orders.getContent();

        return ordersList.stream()
                .map(order -> {
                    OrderItem item = order.getOneOrderItemOrThrow();
                    String username = userReader.usernameById(order.getUserId());
                    ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
                    String status = product.getStatus().getStatus();
                    return OrderResponseDto.from(order,username,status);
                })
                .toList();
    }

    public OrderResponseDto readOneOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        //유저정보 조회
        String username = userReader.usernameById(order.getUserId());

        //상품정보 조회
        OrderItem item = order.getOneOrderItemOrThrow();
        ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
        String status = product.getStatus().getStatus();

        return OrderResponseDto.from(order,username,status);

    }

    public List<OrderResponseDto> readMyOrderList(int page, int size, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = orderRepository.findByUserIdAndIsDeletedFalse(userId,pageable);

        return orders.stream()
                .map(order -> {
                    OrderItem item = order.getOneOrderItemOrThrow();
                    String username = userReader.usernameById(order.getUserId());
                    ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
                    String status = product.getStatus().getStatus();
                    return OrderResponseDto.from(order,username,status);})
                .toList();
        }

    public OrderResponseDto readOneMyOrder(Long id, Long userId) {

        userReader.validateUserIdOrThrow(userId);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        order.validateOrderer(userId);

        OrderItem item = order.getOneOrderItemOrThrow();
        String username = userReader.usernameById(order.getUserId());
        ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
        String status = product.getStatus().getStatus();

        return OrderResponseDto.from(order,username,status);
    }

    @Transactional
    public void deleteOrder(Long id, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        if (!order.getUserId().equals(userId)) {
            throw new OrderException(OrderErrorCode.NOT_SAME_ORDER_USER);
        }

        order.delete();

    }


}
