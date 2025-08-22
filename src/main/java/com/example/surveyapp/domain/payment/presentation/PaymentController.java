package com.example.surveyapp.domain.payment.presentation;

import com.example.surveyapp.domain.payment.application.PaymentService;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.payment.presentation.dto.response.PointChargeResponseDto;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    //포인트 충전
    @PreAuthorize("hasRole('SURVEYOR')")
    @PostMapping("/charge")
    public ResponseEntity<PointChargeResponseDto> charge(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PointChargeRequestDto dto
    ){
        PointChargeResponseDto responseDto = paymentService.charge(userDetails.getId(), dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

}
