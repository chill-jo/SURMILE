package com.example.surveyapp.domain.survey.domain.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SurveyStatus {
    NOT_STARTED("시작 전"),
    IN_PROGRESS("진행 중"),
    PAUSED("일시 중지"),
    DONE("종료");

    private final String status;

    SurveyStatus(String status){
        this.status = status;
    }

    public static SurveyStatus of(String input){
        return Arrays.stream(SurveyStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("설문 상태 오류"));
    }

    public boolean isNotStarted(){
        return this == NOT_STARTED;
    }

    public boolean isInProgress(){
        return this == IN_PROGRESS;
    }

    public boolean isDone(){
        return this == DONE;
    }

    public boolean isPaused(){
        return this == PAUSED;
    }
}
