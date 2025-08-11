package com.example.surveyapp.domain.point.service;

import com.example.surveyapp.domain.order.facade.PointFacade;
import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.point.domain.model.entity.PointHistory;
import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;
import com.example.surveyapp.domain.point.domain.model.enums.PointType;
import com.example.surveyapp.domain.point.domain.model.enums.Target;
import com.example.surveyapp.domain.point.domain.repository.PointHistoryRepository;
import com.example.surveyapp.domain.point.domain.repository.PointRepository;
import com.example.surveyapp.domain.survey.facade.SurveyPointFacade;
import com.example.surveyapp.domain.surveyanswer.facade.SurveyAnswerPointFacade;
import com.example.surveyapp.global.response.exception.CustomException;
import com.example.surveyapp.global.response.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointEarnRedeemService implements PointFacade, SurveyPointFacade, SurveyAnswerPointFacade {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @Transactional
    public void decreasePoint(Long userId, PointPoints amount, Long orderId){

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        //차감 전 포인트
        PointPoints currentBalance=point.getPoints();

        //포인트 차감 (dirty checking)
        point.redeem(amount);

        //포인트 내역 기록
        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPoints(),
                PointType.USAGE,
                Target.ORDER,
                orderId,
                "상품 교환 포인트 차감",
                userId,
                point
        );

        pointHistoryRepository.save(history);
    }

    @Override
    @PreAuthorize("hasAnyRole('SURVEYOR')")
    @Transactional
    public void decreaseSurveyorPoint(Long userId, PointPoints amount, Long surveyId) {

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        PointPoints currentBalance = point.getPoints();

        point.redeem(amount);

        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPoints(),
                PointType.USAGE,
                Target.SURVEY,
                surveyId,
                "설문 생성 포인트 차감",
                userId,
                point
        );

        pointHistoryRepository.save(history);

    }

    @Override
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @Transactional
    public void increaseSurveyeePoint(Long userId, PointPoints amount, Long surveyAnswerId){

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        //적립 전 포인트
        PointPoints currentBalance=point.getPoints();

        //포인트 적립 (dirty checking)
        point.earn(amount);

        //포인트 내역 기록
        PointHistory history = PointHistory.of(
                currentBalance,
                amount,
                point.getPoints(),
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
