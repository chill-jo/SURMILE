package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.event.PointChargeSucceededEvent;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.event.PointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
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

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();

        point.redeem(amount);

        eventPublisher.publishEvent(new PointRedeemSucceededEvent(
                userId,
                orderId ));

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

        pointHistoryRepository.save(history);
    }


    @Transactional
    public void decreaseSurveyorPoint(Long userId, PointBalance amount, Long surveyId) {

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();

        point.redeem(amount);

        SurveyPointRedeemSucceededEvent event = new SurveyPointRedeemSucceededEvent(userId, surveyId);

        eventPublisher.publishEvent(event);

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

        PointBalance currentBalance = point.getPointBalance();

        point.earn(amount);

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

    @Transactional
    public void increaseSurveyorPoint(Long userId, Long paymentId, PointBalance amount){

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();

        point.earn(amount);

        eventPublisher.publishEvent(new PointChargeSucceededEvent(
                userId,
                paymentId
        ));

        log.info("이벤트 발행");

        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPointBalance(),
                PointType.CHARGE,
                Target.PAYMENTS,
                paymentId,
                "포인트 충전",
                userId,
                point
        );

        pointHistoryRepository.save(history);

    }

}
