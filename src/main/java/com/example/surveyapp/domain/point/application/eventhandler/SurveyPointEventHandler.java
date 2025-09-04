package com.example.surveyapp.domain.point.application.eventhandler;

import com.example.surveyapp.domain.point.application.PointEarnRedeemService;
import com.example.surveyapp.domain.point.domain.event.SurveyPointRedeemFailedEvent;
import com.example.surveyapp.domain.point.domain.model.vo.PointBalance;
import com.example.surveyapp.domain.point.domain.model.entity.PointOutbox;
import com.example.surveyapp.domain.point.domain.repository.PointOutboxRepository;
import com.example.surveyapp.domain.point.exception.PointErrorCode;
import com.example.surveyapp.domain.point.exception.PointException;
import com.example.surveyapp.domain.survey.domain.event.SurveyCreateEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyPointEventHandler {

    private final PointEarnRedeemService pointEarnRedeemService;
    private final ObjectMapper objectMapper;
    private final PointOutboxRepository pointOutboxRepository;

    public void handleSurveyCreateEvent(SurveyCreateEvent event){
        try{
            pointEarnRedeemService.decreaseSurveyorPoint(
                    event.getUserId(),
                    PointBalance.of(event.getTotalPoint()),
                    event.getSurveyId()
            );
        } catch (Exception e) {
            SurveyPointRedeemFailedEvent pointFailedEvent = new SurveyPointRedeemFailedEvent(event.getUserId(), event.getSurveyId());
            publishOutbox(pointFailedEvent);
        }
    }

    public void handleSurveyAnswerEvent(SurveyAnswerEvent event){
        pointEarnRedeemService.increaseSurveyeePoint(
                event.getUserId(),
                PointBalance.of(event.getPointPerPerson()),
                event.getSurveyAnswerId());
    }

    private void publishOutbox(SurveyPointRedeemFailedEvent event){
        PointOutbox pointOutbox = PointOutbox.of(
                "Survey-Fail",
                event.getSurveyId(),
                toJson(event)
        );
        pointOutboxRepository.save(pointOutbox);
    }

    private String toJson(Object event){
        try{
            return objectMapper.writeValueAsString(event);
        }catch(JsonProcessingException e){
            throw new PointException(PointErrorCode.CANNOT_CONVERT_PAYLOAD);
        }
    }
}
