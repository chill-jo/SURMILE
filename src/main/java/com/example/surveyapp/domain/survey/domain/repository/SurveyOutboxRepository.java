package com.example.surveyapp.domain.survey.domain.repository;

import com.example.surveyapp.domain.survey.domain.model.entity.SurveyOutbox;
import com.example.surveyapp.domain.survey.domain.model.enums.SurveyOutboxEnum;

import java.util.List;

public interface SurveyOutboxRepository {

    SurveyOutbox save(SurveyOutbox surveyOutbox);

    List<SurveyOutbox> findByStatusAndPublished(SurveyOutboxEnum status, boolean published);
}
