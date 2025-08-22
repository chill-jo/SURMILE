package com.example.surveyapp.domain.order.application;

import com.example.surveyapp.domain.order.application.mapper.OrderResponseMapper;
import com.example.surveyapp.domain.order.domain.model.OrderOutbox;
import com.example.surveyapp.domain.order.domain.repository.OrderOutboxRepository;
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
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserReader userReader;
    private final ProductFacade productFacade;
    private final OrderResponseMapper orderResponseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto requestDto, Long userId) {
        userReader.validateUserIdOrThrow(userId);

        ProductInfoDto product = productFacade.findProductInfo(requestDto.getProductId());

        OrderItem item = OrderItem.of(requestDto.getProductId(),
                product.getTitle(),
                OrderItemPoints.of(product.getPrice()));

        Order order = Order.of(
                userId,
                item
                );

        String status = product.getStatus().getStatus();

        Order saveOrder = orderRepository.save(order);

        try {
            OrderCreateEvent event = new OrderCreateEvent(saveOrder.getId(),
                    userId,
                    order.orderAmount());

            String payload = objectMapper.writeValueAsString(event);

            OrderOutbox orderOutbox = OrderOutbox.of("Order",
                    saveOrder.getId(),
                    payload
                    );

            orderOutboxRepository.save(orderOutbox);

        } catch (JsonProcessingException e) {
            throw new OrderException(OrderErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

       return OrderCreateResponseDto.from(saveOrder,status);
    }

    public Page<OrderResponseDto> readAllOrder(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return orderRepository.findAll(pageable)
                .map(orderResponseMapper::toDto);
    }

    public OrderResponseDto readOneOrder(Long id) {

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        return orderResponseMapper.toDto(order);

    }

    public Page<OrderResponseDto> readMyOrderList(int page, int size, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Pageable pageable = PageRequest.of(page,size);

        return orderRepository.findByUserIdAndIsDeletedFalse(userId,pageable)
                .map(orderResponseMapper::toDto);
    }

    public OrderResponseDto readOneMyOrder(Long id, Long userId) {

        userReader.validateUserIdOrThrow(userId);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        order.validateOrderer(userId);

        return orderResponseMapper.toDto(order);
    }

    @Transactional
    public void deleteOrder(Long id, Long userId) {
        userReader.validateUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new OrderException(OrderErrorCode.NOT_FOUND_ORDER));

        order.validateOrderer(userId);
        order.delete();

    }


}
