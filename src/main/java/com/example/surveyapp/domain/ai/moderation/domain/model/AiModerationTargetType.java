package com.example.surveyapp.domain.ai.moderation.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiModerationTargetType {
    USER_NICKNAME("사용자 닉네임"),
    SURVEY_TITLE("설문 제목"),
    SURVEY_DESCRIPTION("설문 부제목"),
    SURVEY_QUESTION("설문 질문"),
    SURVEY_OPTIONS("설문 선택지"),
    SURVEY_TEXT_ANSWER("설문 서술형 답안");

    private final String description;
}
