package com.example.surveyapp.domain.surveyanswer.domain.repository;

import com.example.surveyapp.domain.surveyanswer.domain.model.entity.AnserEnum;
import com.example.surveyapp.domain.surveyanswer.domain.model.entity.SurveyAnswerOutbox;

import java.util.List;

public interface SurveyAnswerOutboxRepository {
    SurveyAnswerOutbox save(SurveyAnswerOutbox surveyAnswerOutbox);

    List<SurveyAnswerOutbox> findByStatusAndPublished(AnserEnum status, boolean published);
}
