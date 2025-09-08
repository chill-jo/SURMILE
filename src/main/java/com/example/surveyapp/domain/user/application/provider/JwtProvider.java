package com.example.surveyapp.domain.user.application.provider;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;

public interface JwtProvider {

    String extractUserId(String token);

    String substringToken(String token);

    String createServiceToken(Long userId, String serviceName);

    String createRefreshToken(Long userId);

    String createAccessToken(Long userId, UserRoleEnum userRole);

    boolean isValidRefreshToken(String token);

}
