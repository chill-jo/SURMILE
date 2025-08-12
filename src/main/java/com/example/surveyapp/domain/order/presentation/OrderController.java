package com.example.surveyapp.domain.order.presentation;

import com.example.surveyapp.domain.order.presentation.dto.OrderCreateRequestDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderCreateResponseDto;
import com.example.surveyapp.domain.order.presentation.dto.OrderResponseDto;
import com.example.surveyapp.domain.order.application.OrderService;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> create(@Valid@RequestBody OrderCreateRequestDto requestDto,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        OrderCreateResponseDto order = orderService.createOrder(requestDto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("주문이 완료되었습니다.",order));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<ApiResponse<List<OrderResponseDto>>> readAllOrder(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<OrderResponseDto> orderList = orderService.readAllOrder(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문목록을 조회 하였습니다.",orderList));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> readOneOrder(@PathVariable Long id) {
        OrderResponseDto responseDto = orderService.readOneOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 단 건 조회가 완료되었습니다.",responseDto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> readMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        List<OrderResponseDto> myOrderList = orderService.readMyOrderList(page,size,userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("내 주문 목록 조회 하였습니다.",myOrderList));
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> readOneMyOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        OrderResponseDto responseDto = orderService.readOneMyOrder(id,userId);
       return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("내 주문 단건 조회하였습니다.",responseDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SURVEYEE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getId();
        orderService.deleteOrder(id,userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 내역이 삭제되었습니다.",null));

    }
}
