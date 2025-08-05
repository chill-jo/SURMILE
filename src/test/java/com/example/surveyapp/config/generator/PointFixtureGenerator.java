package com.example.surveyapp.config.generator;

import com.example.surveyapp.domain.point.domain.model.entity.Point;
import com.example.surveyapp.domain.user.domain.model.User;
import org.springframework.test.util.ReflectionTestUtils;

public class PointFixtureGenerator {


    public static Point generatePointFixture(User user) {
        Point point = Point.of(user);
        ReflectionTestUtils.setField(point, "id", 1L);
        return point;
    }


    public static Point generatePointFixture(User user, long initialBalance) {
        Point point = Point.of(user);
        point.pointCharge(initialBalance);
        ReflectionTestUtils.setField(point, "id", 1L);
        return point;
    }
}
