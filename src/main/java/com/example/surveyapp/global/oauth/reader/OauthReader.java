package com.example.surveyapp.global.oauth.reader;

public interface OauthReader {
    void validateUserIdOrThrow(Long userId);
    String usernameById(Long userId);
    boolean validateUserRoleToAdmin(Long userId);
    boolean validateUserRoleToSurveyee(Long userId);
    boolean validateUserRoleToSurveyor(Long userId);
    void validateUserEmailOrThrow(String email);
    String usernameByEmail(String email);
    boolean validateUserRoleToAdminByEmail(String email);
    boolean validateUserRoleToSurveyeeByEmail(String email);
    boolean validateUserRoleToSurveyorByEmail(String email);
}