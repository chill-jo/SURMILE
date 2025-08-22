package com.example.surveyapp.domain.payment.application;

import com.example.surveyapp.domain.payment.domain.event.PointChargeEvent;
import com.example.surveyapp.domain.payment.domain.model.entity.Payment;
import com.example.surveyapp.domain.payment.domain.model.enums.*;
import com.example.surveyapp.domain.payment.domain.model.vo.Money;
import com.example.surveyapp.domain.payment.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.payment.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.payment.presentation.dto.response.PointChargeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        eventPublisher.publishEvent(
                new PointChargeEvent(
                        userId,
                        payment.getId(),
                        requestDto.getPrice()
                ));

        return new PointChargeResponseDto(
                payment.getId(),
                payment.getAmount().getValue(),
                payment.getPointStatus(),
                payment.getMethod()
        );
    }
}
