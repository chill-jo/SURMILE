package com.example.surveyapp.global.security.jwt;

import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private final JwtUtil jwtUtil;

    @Override
    public String extractUserId(String token) {
        return jwtUtil.extractAllClaims(token).getSubject();
    }

    @Override
    public String substringToken(String tokenValue) {
        return jwtUtil.substringToken(tokenValue);
    }

    @Override
    public String createServiceToken(Long userId, String serviceName){
        return jwtUtil.createServiceToken(userId, serviceName);
    }

    @Override
    public String createRefreshToken(Long userId) {

        return jwtUtil.createRefreshToken(userId);
    }

    @Override
    public String createAccessToken(Long userId, UserRoleEnum userRole) {
        return jwtUtil.createAccessToken(userId, userRole);
    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        return jwtUtil.isValidRefreshToken(refreshToken);
    }
}
