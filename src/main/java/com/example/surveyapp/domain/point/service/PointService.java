package com.example.surveyapp.domain.point.service;

import com.example.surveyapp.domain.point.controller.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.controller.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.domain.model.entity.Payment;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.enums.*;
import com.example.surveyapp.domain.point.domain.repository.PaymentRepository;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;


    // 충전
    @PreAuthorize("hasAnyRole('SURVEYOR')")
    @Transactional
    public void charge(Long userId, PointChargeRequestDto dto){
        //요청받은 금액
        Long price=dto.getPrice();

        Point point = getPoint(userId);

        //충전 전 금액
        Long currentBalance=point.getPointBalance().getValue();

        //포인트 충전. (dirty checking)
        point.pointCharge(price);

        Payment payment = Payment.of(
                price,
                PointStatus.DONE,
                Method.KAKAO_PAY,
                TargetType.POINT_CHARGE
        );
        paymentRepository.save(payment);

        PointHistory history = PointHistory.of(
                currentBalance,
                price,
                point.getPointBalance().getValue(),
                PointType.CHARGE,
                Target.PAYMENTS,
                payment.getId(),
                "포인트 충전",
                userId,
                point
        );

        pointHistoryRepository.save(history);


    }


    // 설문 응답하는 경우 적립
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @Transactional
    public void earn(Long userId, Long amount, Long surveyAnswerId){

        Point point = getPoint(userId);

        //적립 전 포인트
        Long currentBalance=point.getPointBalance().getValue();

        //포인트 적립 (dirty checking)
        point.earn(amount);

        //포인트 내역 기록
        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance().getValue(),
                PointType.EARN,
                Target.SURVEY,
                surveyAnswerId,
                "설문 응답 포인트 적립",
                userId,
                point
        );

        pointHistoryRepository.save(history);
    }


    @PreAuthorize("hasAnyRole('SURVEYOR')")
    @Transactional
    public void surveyorRedeem(Long userId, Long amount, Long surveyId){

        Point point = getPoint(userId);

        Long currentBalance = point.getPointBalance().getValue();

        point.redeem(amount);

        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance().getValue(),
                PointType.USAGE,
                Target.SURVEY,
                surveyId,
                "설문 생성 포인트 차감",
                userId,
                point
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
    private Point getPoint(Long userId){
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));
    }

}
