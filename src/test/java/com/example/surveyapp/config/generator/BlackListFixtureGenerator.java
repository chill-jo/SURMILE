package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.admin.domain.model.BlackList;
import com.example.surveyapp.domain.user.domain.model.User;

public class BlackListFixtureGenerator {

    public static BlackList generateBlackListFixture() {
        User user = UserFixtureGenerator.generateUserFixture();
        return new BlackList(user);
    }
}
