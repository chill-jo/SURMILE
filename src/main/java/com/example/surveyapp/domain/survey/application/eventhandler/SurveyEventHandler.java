package com.example.surveyapp.domain.survey.application.eventhandler;

import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemSucceededEvent;
import com.example.surveyapp.domain.survey.application.SurveyQueryService;
import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurveyEventHandler {

    private final SurveyQueryService surveyQueryService;

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyDoneEvent(SurveyDoneEvent event){
        surveyQueryService.findSurvey(event.getSurveyId())
                .changeSurveyStatus(SurveyStatus.DONE);
    }

    @Async
    @Transactional
    @EventListener
    public void handlePointRedeemFailEvent(SurveyPointRedeemFailedEvent event){
        log.info("포인트 차감 실패 이벤트 받음");
        Survey survey = surveyQueryService.findSurvey(event.getSurveyId());

        survey.changeSurveyStatus(SurveyStatus.CANCELLED);
        survey.deleteSurvey();
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    public void handlePointRedeemSuccessEvent(SurveyPointRedeemSucceededEvent event){
        log.info("포인트 차감 성공 이벤트 받음");
        Survey survey = surveyQueryService.findSurvey(event.getSurveyId());

        survey.changeSurveyStatus(SurveyStatus.NOT_STARTED);
    }
}
