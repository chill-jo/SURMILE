package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.admin.domain.model.BlackList;

public class BlackListFixtureGenerator {

    public static BlackList generateBlackListFixture() {
        Long userId = 1L;
        return BlackList.of(userId);
    }
}
