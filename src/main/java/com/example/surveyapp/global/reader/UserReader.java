package com.example.surveyapp.global.reader;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;

public interface UserReader {
    void validateUserIdOrThrow(Long userId);
    boolean isSurveyor(Long userId);
}
