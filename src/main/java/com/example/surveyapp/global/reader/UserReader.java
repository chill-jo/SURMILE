package com.example.surveyapp.global.reader;

public interface UserReader {
    void validateUserIdOrThrow(Long userId);
    String usernameById(Long userId);
    boolean validateUserRoleToAdmin(Long userId);
    boolean validateUserRoleToSurveyee(Long userId);
    boolean validateUserRoleToSurveyor(Long userId);
    String usernameByEmail(String email);
    boolean validateUserRoleToSurveyorByEmail(String userRole, String email);
}