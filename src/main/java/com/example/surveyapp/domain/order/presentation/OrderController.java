package com.example.surveyapp.domain.order.presentation;

import com.example.surveyapp.domain.order.presentation.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.application.OrderService;

import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<OrderCreateResponseDto> create(@Valid@RequestBody OrderCreateRequestDto requestDto,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        OrderCreateResponseDto order = orderService.createOrder(requestDto,userId);
        URI location = URI.create("/api/orders" + order.getId());
        return ResponseEntity.accepted().location(location).body(order);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<Page<OrderResponseDto>> readAllOrder(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponseDto> orderList = orderService.readAllOrder(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(orderList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> readOneOrder(@PathVariable Long id) {
        OrderResponseDto responseDto = orderService.readOneOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<Page<OrderResponseDto>> readMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        Page<OrderResponseDto> myOrderList = orderService.readMyOrderList(page,size,userId);
        return ResponseEntity.status(HttpStatus.OK).body(myOrderList);
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<OrderResponseDto> readOneMyOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        OrderResponseDto responseDto = orderService.readOneMyOrder(id,userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getId();
        orderService.deleteOrder(id,userId);
        return ResponseEntity
                .status(HttpStatus.OK).body(null);
    }
}
