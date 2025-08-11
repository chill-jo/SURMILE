package com.example.surveyapp.domain.point.event;

import com.example.surveyapp.domain.point.domain.model.entity.PointPoints;
import com.example.surveyapp.domain.survey.event.SurveyCreateEvent;
import com.example.surveyapp.domain.survey.facade.SurveyPointFacade;
import com.example.surveyapp.domain.surveyanswer.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.facade.SurveyAnswerPointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SurveyPointEventHandler {

    private final SurveyPointFacade surveyPointFacade;
    private final SurveyAnswerPointFacade surveyAnswerPointFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyCreateEvent(SurveyCreateEvent event){
        surveyPointFacade.decreaseSurveyorPoint(
                event.getUserId(),
                PointPoints.create(event.getSurvey().getSurveyInfo().getTotalPoint().getValue()),
                event.getSurvey().getId());

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSurveyAnswerEvent(SurveyAnswerEvent event){
        surveyAnswerPointFacade.increaseSurveyeePoint(
                event.getUserId(),
                PointPoints.create(event.getSurvey().getSurveyInfo().getTotalPoint().getValue()),
                event.getSurveyAnswerId());
    }
}
