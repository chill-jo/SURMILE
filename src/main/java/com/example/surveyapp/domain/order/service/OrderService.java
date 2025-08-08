package com.example.surveyapp.domain.order.service;

import com.example.surveyapp.domain.order.controller.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.controller.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.controller.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.model.Order;
import com.example.surveyapp.domain.order.model.repository.OrderRepository;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.point.service.PointService;
import com.example.surveyapp.domain.product.domain.model.Product;
import com.example.surveyapp.domain.product.domain.model.repository.ProductRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final ProductRepository productRepository;
    private final PointService pointService;


    @Transactional
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Point point = pointRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POINT));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        if (point.getPointBalance() < product.getPrice()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }

        Order order = Order.create(user,
                product
                );

        Order saveOrder = orderRepository.save(order);

        //포인트 차감 로직
        pointService.redeem(user.getId(),product.getPrice(), saveOrder.getId());

        return OrderCreateResponseDto.from(saveOrder);
    }

    public List<OrderResponseDto> readAllOrder(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAll(pageable);
        List<Order> ordersList = orders.getContent();

        return ordersList.stream().map(OrderResponseDto::from)
                .toList();
    }

    public OrderResponseDto readOneOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        return OrderResponseDto.from(order);

    }

    public List<OrderResponseDto> readMyOrderList(int page, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Pageable pageable = PageRequest.of(page,size);
        Page<Order> orders = orderRepository.findByUser(user,pageable);

        return orders.stream()
                .map(OrderResponseDto::from)
                .toList();
        }

    public OrderResponseDto readOneMyOrder(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        if (!order.getUser().getId().equals(user.getId())){
            throw new CustomException(ErrorCode.NOT_YOUR_ORDER);
        }

        return OrderResponseDto.from(order);
    }

    @Transactional
    public void deleteOrder(Long id, Long userId) {
        if (userId == null) {
                throw  new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_SAME_ORDER_USER);
        }

        order.delete();

    }


}
