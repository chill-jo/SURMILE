package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.survey.domain.model.vo.SurveyInfo;
import com.example.surveyapp.domain.survey.domain.model.vo.SurveyPoints;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class SurveyInfoFixtureGenerator {
    public static final String title = "테스트 설문 제목";
    public static String description = "테스트 설문 설명";
    public static Long maxSurveyee = 50L;
    public static Long pointPerPerson = 100L;
    public static LocalDateTime deadline = LocalDateTime.of(2025, 9,25, 15,30);
    public static Long expectedTime = 20L;

    public static SurveyInfo generateSurveyInfoFixture(){
        SurveyInfo surveyInfo = getSurveyInfo(title, description, maxSurveyee, pointPerPerson, deadline, expectedTime);

        ReflectionTestUtils.setField(surveyInfo, "totalPoint", SurveyPoints.of(5000L));

        return surveyInfo;
    }

    public static SurveyInfo getSurveyInfo(String title, String description, Long maxSurveyee, Long pointPerPerson,
                                LocalDateTime deadline, Long expectedTime){

        return new SurveyInfo(title, description, maxSurveyee, SurveyPoints.of(pointPerPerson), deadline, expectedTime);
    }
}
