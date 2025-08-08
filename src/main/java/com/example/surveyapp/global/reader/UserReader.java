package com.example.surveyapp.global.reader;

public interface UserReader {
    void validateUserIdOrThrow(Long userId);
    boolean isSurveyor(Long userId);
    String usernameById(Long userId);
}