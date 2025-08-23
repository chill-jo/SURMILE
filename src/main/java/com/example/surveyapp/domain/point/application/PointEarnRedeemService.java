package com.example.surveyapp.domain.point.application;

import com.example.surveyapp.domain.point.domain.event.PointChargeSucceededEvent;
import com.example.surveyapp.domain.point.domain.event.PointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemSucceededEvent;
import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final PointOutboxRepository pointOutboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void decreasePoint(Long userId, PointBalance amount, Long orderId){

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();
        point.redeem(amount);


        try {
            PointRedeemSucceededEvent event =
                    new PointRedeemSucceededEvent(orderId, userId);

            String payload = objectMapper.writeValueAsString(event);

            PointOutbox pointOutbox = PointOutbox.of(
                    "Order-Success",
                    point.getId(),
                    payload);

            pointOutboxRepository.save(pointOutbox);

        } catch (JsonProcessingException e) {
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

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

        try{
            SurveyPointRedeemSucceededEvent event = new SurveyPointRedeemSucceededEvent(userId, surveyId);

            String payload = objectMapper.writeValueAsString(event);

            PointOutbox pointOutbox = PointOutbox.of("Survey-Success",
                    point.getId(),
                    payload);

            pointOutboxRepository.save(pointOutbox);
        } catch (JsonProcessingException e) {
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

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
    public void increaseSurveyorPoint(Long userId, Long paymentId, PointBalance amount) {

        PointWallet point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));

        PointBalance currentBalance = point.getPointBalance();

        point.earn(amount);

        try{
            PointChargeSucceededEvent event = new PointChargeSucceededEvent(userId, paymentId);

            String payload = objectMapper.writeValueAsString(event);

            PointOutbox pointOutbox = PointOutbox.of("Payment-Success",
                    point.getId(),
                    payload);

            pointOutboxRepository.save(pointOutbox);
        } catch (JsonProcessingException e) {
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }

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

    private String toJson(Object event){
        try{
            return objectMapper.writeValueAsString(event);
        } catch(JsonProcessingException e){
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }
    }

}
