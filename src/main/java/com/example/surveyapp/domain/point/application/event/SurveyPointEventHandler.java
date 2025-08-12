package com.example.surveyapp.domain.point.application.event;

import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.model.entity.vo.PointBalance;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SurveyPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyCreateEvent(SurveyCreateEvent event){
        pointEarnRedeemService.decreaseSurveyorPoint(
                event.getUserId(),
                PointBalance.of(event.getSurvey().getSurveyInfo().getTotalPoint().getValue()),
                event.getSurvey().getId());

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
