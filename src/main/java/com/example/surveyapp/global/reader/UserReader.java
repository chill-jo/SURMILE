package com.example.surveyapp.global.reader;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;

public interface UserReader {
    void validateUserIdOrThrow(Long userId);
    String usernameById(Long userId);
    boolean validateUserRoleToAdmin(Long userId);
    boolean validateUserRoleToSurveyee(Long userId);
    boolean validateUserRoleToSurveyor(Long userId);
}