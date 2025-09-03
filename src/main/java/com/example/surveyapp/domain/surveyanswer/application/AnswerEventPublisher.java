package com.example.surveyapp.domain.surveyanswer.application;


import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyAnswerEvent;
import com.example.surveyapp.domain.surveyanswer.domain.event.SurveyDoneEvent;

public interface AnswerEventPublisher {
    void answerPublishEvent(SurveyAnswerEvent event);
    void donePublishEvent(SurveyDoneEvent event);
}
