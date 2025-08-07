package com.example.surveyapp.domain.order.service;

import com.example.surveyapp.domain.order.controller.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.controller.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.facade.PointFacade;
import com.example.surveyapp.domain.order.facade.ProductFacade;
import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.OrderItem;
import com.example.surveyapp.domain.order.model.repository.OrderRepository;;
import com.example.surveyapp.domain.product.controller.dto.ProductInfoDto;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
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

        OrderItem item = OrderItem.create(requestDto.getProductId(),
                product.getTitle(),
                product.getPrice());

        Order order = Order.create(
                userId,
                List.of(item)
                );

        String status = product.getStatusName();

        Order saveOrder = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreateEvent(saveOrder,userId));

        return OrderCreateResponseDto.from(saveOrder,status);
    }

    public List<OrderResponseDto> readAllOrder(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAll(pageable);
        List<Order> ordersList = orders.getContent();

        return ordersList.stream()
                .map(order -> {
                    OrderItem item = order.onlyOneOrderItem();
                    String username = userReader.usernameById(order.getUserId());
                    ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
                    String status = product.getStatusName();
                    return OrderResponseDto.from(order,username,status);
                })
                .toList();
    }

    public OrderResponseDto readOneOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        //유저정보 조회
        String username = userReader.usernameById(order.getUserId());

        //상품정보 조회
        OrderItem item = order.onlyOneOrderItem();
        ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
        String status = product.getStatusName();

        return OrderResponseDto.from(order,username,status);

    }

    public List<OrderResponseDto> readMyOrderList(int page, int size, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = orderRepository.findByUser(userId,pageable);

        return orders.stream()
                .map(order -> {
                    OrderItem item = order.onlyOneOrderItem();
                    String username = userReader.usernameById(order.getUserId());
                    ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
                    String status = product.getStatusName();
                    return OrderResponseDto.from(order,username,status);})
                .toList();
        }

    public OrderResponseDto readOneMyOrder(Long id, Long userId) {

        userReader.validateUserIdOrThrow(userId);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        if (!order.getUserId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_YOUR_ORDER);
        }

        OrderItem item = order.onlyOneOrderItem();
        String username = userReader.usernameById(order.getUserId());
        ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
        String status = product.getStatusName();

        return OrderResponseDto.from(order,username,status);
    }

    @Transactional
    public void deleteOrder(Long id, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_SAME_ORDER_USER);
        }

        order.delete();

    }


}
