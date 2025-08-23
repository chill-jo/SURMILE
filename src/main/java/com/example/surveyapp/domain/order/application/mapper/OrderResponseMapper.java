package com.example.surveyapp.domain.order.application.mapper;

import com.example.surveyapp.domain.order.application.facade.ProductFacade;
import com.example.surveyapp.domain.order.domain.model.Order;
import com.example.surveyapp.domain.order.domain.model.vo.OrderItem;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.product.presentation.dto.ProductInfoDto;
import com.example.surveyapp.global.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderResponseMapper {

    private final UserReader userReader;
    private final ProductFacade productFacade;


    public OrderResponseDto toDto(Order order) {
        OrderItem item = order.getOrderItem();
        String username = userReader.usernameById(order.getUserId());
        ProductInfoDto product = productFacade.findProductInfo(item.getProductId());
        String status = product.getStatus().getStatus();
        return OrderResponseDto.from(order,username,status);
    }

}
