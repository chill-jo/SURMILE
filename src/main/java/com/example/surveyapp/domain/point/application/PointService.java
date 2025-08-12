package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.point.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.domain.model.entity.*;
import com.example.surveyapp.domain.point.domain.model.entity.vo.Money;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.*;
import com.example.surveyapp.domain.point.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PointService {

    private final MoneyToPointService moneyToPointService;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentRepository paymentRepository;

    // 충전
    @PreAuthorize("hasAnyRole('SURVEYOR')")
    @Transactional
    public void charge(Long userId, PointChargeRequestDto dto){
        //요청받은 금액

        Money amount = Money.krw(dto.getPrice());

        PointBalance chargePoint = moneyToPointService.convert(amount);

        PointWallet pointWallet = getPoint(userId);

        //충전 전 금액
        PointBalance currentBalance = pointWallet.getPointBalance();

        //포인트 충전. (dirty checking)
        pointWallet.pointCharge(chargePoint);

        Payment payment = Payment.of(
                amount,
                PointStatus.DONE,
                Method.KAKAO_PAY,
                TargetType.POINT_CHARGE
        );
        paymentRepository.save(payment);

        PointHistory history = PointHistory.of(
                currentBalance,
                chargePoint,
                pointWallet.getPointBalance(),
                PointType.CHARGE,
                Target.PAYMENTS,
                payment.getId(),
                "포인트 충전",
                userId,
                pointWallet
        );

        pointHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public Page<PointHistoryResponseDto> getHistories(Long userId, Pageable pageable){

        return pointHistoryRepository.findPointHistoryByUserId(userId, pageable)
                .map(PointHistoryResponseDto::from);
    }

    /**
     * 공통되는 로직을 메서드로 분리
     * 해당하는 userId로 포인트를 조회한다.
     * 조회하지 못하는 경우 CustomException 발생
     * @param userId
     * @return
     */
    private PointWallet getPoint(Long userId){
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
    }

}
