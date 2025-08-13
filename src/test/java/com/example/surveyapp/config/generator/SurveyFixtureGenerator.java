package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.survey.domain.model.entity.Survey;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import org.springframework.test.util.ReflectionTestUtils;

public class SurveyFixtureGenerator {
    public static final Long userId = 1L;

    public static Survey generateSurveyFixture(){
        SurveyInfo surveyInfo = SurveyInfoFixtureGenerator.generateSurveyInfoFixture();

        Survey survey = getSurvey(userId, surveyInfo);

        ReflectionTestUtils.setField(survey, "id", 1L);

        return survey;
    }

    public static Survey getSurvey(Long userId, SurveyInfo surveyInfo){

        return Survey.of(userId, surveyInfo);
    }
}
