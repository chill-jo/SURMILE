package com.example.surveyapp.domain.survey.application.event;

import com.example.surveyapp.domain.survey.application.SurveyQueryService;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyStatus;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SurveyEventHandler {

    private final SurveyQueryService surveyQueryService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyDoneEvent(SurveyDoneEvent event){
        surveyQueryService.findSurvey(event.getSurveyId())
                .changeSurveyStatus(SurveyStatus.DONE);
    }
}
