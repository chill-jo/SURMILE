package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.event.PointRedeemSuccessEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.entity.event.PointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnRedeemService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void decreasePoint(Long userId, PointBalance amount, Long orderId){
        log.info("[decreasePoint] start userId={}, amount={}, orderId={}", userId, amount.getValue(), orderId);

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        log.info("[decreasePoint] loaded wallet balance={}, userId={}", point.getPointBalance().getValue(), userId);

        //차감 전 포인트
        PointBalance currentBalance = point.getPointBalance();

        //포인트 차감 (dirty checking)
        point.redeem(amount);
        log.info("[decreasePoint] redeem OK. newBalance={}", point.getPointBalance().getValue());

        eventPublisher.publishEvent(new PointRedeemSucceededEvent(orderId,
                userId));
        log.info("포인트 차감 메서드 성공 후 이벤트 발행");
        log.info("[decreasePoint] 성공 이벤트 발행");


        //포인트 내역 기록
        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance(),
                PointType.USAGE,
                Target.ORDER,
                orderId,
                "상품 교환 포인트 차감",
                userId,
                point
        );
        log.info("[decreasePoint] history saved");

        log.info("포인트 차감 메서드 실행");

        pointHistoryRepository.save(history);
    }

    @Transactional
    public void decreaseSurveyorPoint(Long userId, PointBalance amount, Long surveyId) {

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();

        point.redeem(amount);

        eventPublisher.publishEvent(
                new PointRedeemSuccessEvent(
                        userId,
                        surveyId
                )
        );

        log.info("포인트 차감 성공 이벤트 발행");

        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance(),
                PointType.USAGE,
                Target.SURVEY,
                surveyId,
                "설문 생성 포인트 차감",
                userId,
                point
        );

        pointHistoryRepository.save(history);

    }

    @Transactional
    public void increaseSurveyeePoint(Long userId, PointBalance amount, Long surveyAnswerId){

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        //적립 전 포인트
        PointBalance currentBalance=point.getPointBalance();

        //포인트 적립 (dirty checking)
        point.earn(amount);

        //포인트 내역 기록
        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance(),
                PointType.EARN,
                Target.SURVEY,
                surveyAnswerId,
                "설문 응답 포인트 적립",
                userId,
                point
        );

        pointHistoryRepository.save(history);
    }


}
