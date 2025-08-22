package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.payment.domain.model.enums.*;
import com.example.surveyapp.domain.payment.domain.model.vo.Money;
import com.example.surveyapp.domain.payment.domain.repository.PaymentOutboxRepository;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.payment.exception.PaymentErrorCode;
import com.example.surveyapp.domain.payment.exception.PaymentException;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.payment.presentation.dto.response.PointChargeResponseDto;
import com.example.surveyapp.domain.payment.domain.model.entity.PaymentOutbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PointChargeResponseDto charge(Long userId, PointChargeRequestDto requestDto){

        Money amount = Money.krw(requestDto.getPrice());

        Payment payment = Payment.of(
                amount,
                PointStatus.PENDING,
                Method.KAKAO_PAY,
                TargetType.POINT_CHARGE
        );

        paymentRepository.save(payment);

        try{
            PointChargeEvent event = new PointChargeEvent(
                    userId,
                    payment.getId(),
                    requestDto.getPrice()
            );

            String payload = objectMapper.writeValueAsString(event);

            PaymentOutbox paymentOutbox = PaymentOutbox.of(
                    "Payment",
                    payment.getId(),
                    payload);

            paymentOutboxRepository.save(paymentOutbox);
        } catch (JsonProcessingException e) {
            throw new PaymentException(PaymentErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

        return new PointChargeResponseDto(
                payment.getId(),
                payment.getAmount().getValue(),
                payment.getPointStatus(),
                payment.getMethod()
        );
    }
}
