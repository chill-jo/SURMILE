package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.point.domain.model.entity.PointWallet;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.test.util.ReflectionTestUtils;

public class PointFixtureGenerator {


    public static PointWallet generatePointFixture(Long userId) {
        PointWallet point = PointWallet.of(userId);
        ReflectionTestUtils.setField(point, "id", 1L);
        return point;
    }

}
