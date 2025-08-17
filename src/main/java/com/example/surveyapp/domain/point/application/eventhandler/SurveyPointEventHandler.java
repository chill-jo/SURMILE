package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.PointRedeemFailEvent;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyCreateEvent(SurveyCreateEvent event){
        try{
            pointEarnRedeemService.decreaseSurveyorPoint(
                    event.getUserId(),
                    PointBalance.of(event.getTotalPoint()),
                    event.getSurveyId()
            );

        } catch (Exception e) {
            log.error("포인트 차감 실패 - suveyId={}, errorCode={}",
                    event.getSurveyId(), e.getMessage());

            eventPublisher.publishEvent(
                new PointRedeemFailEvent(
                        event.getUserId(),
                        event.getSurveyId()
                )
            );
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyAnswerEvent(SurveyAnswerEvent event){
        pointEarnRedeemService.increaseSurveyeePoint(
                event.getUserId(),
                PointBalance.of(event.getPointPerPerson()),
                event.getSurveyAnswerId());
    }
}
