package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyAnswerEventHandler {

    private final PointOutboxRepository pointOutboxRepository;
    private final PointEarnRedeemService pointEarnRedeemService;
    private final ObjectMapper objectMapper;

    public void handleAnswerCreateEvent(SurveyAnswerEvent event) {
        try {
            pointEarnRedeemService.increaseSurveyeePoint(event.getUserId(),
                    PointBalance.of(event.getPointPerPerson()),
                    event.getSurveyAnswerId());
        }
        catch (PointException e) {
            SurveyAnswerEvent surveyAnswerEvent = new SurveyAnswerEvent(event.getUserId(),
                    event.getPointPerPerson(),
                    event.getSurveyAnswerId());
            publishOutbox(surveyAnswerEvent);
        }
        catch (Exception e) {
            SurveyAnswerEvent surveyAnswerEvent = new SurveyAnswerEvent(event.getUserId(),
                    event.getPointPerPerson(),
                    event.getSurveyAnswerId());
            publishOutbox(surveyAnswerEvent);
        }
    }

    private void publishOutbox(SurveyAnswerEvent event){
        PointOutbox pointOutbox = PointOutbox.of("Answer-Fail",
                event.getSurveyAnswerId(),
                toJson(event));
        pointOutboxRepository.save(pointOutbox);
    }
    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }
    }

}
