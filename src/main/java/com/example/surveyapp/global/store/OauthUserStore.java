package com.example.surveyapp.global.store;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.AuthProvider;

import java.util.Optional;

public interface OauthUserStore {
    // 기존 계정에 소셜 계정 연결
    User linkIfNeeded(Long userId, String provider, String providerId);

    // 소셜 계정으로 신규 유저 생성
    User createSocialUser(String email, String name, String provider, String providerId);

    // 소셜 계정으로 유저 조회
    Optional<User> findByProvider(String provider, String providerId);

    // 이메일로 유저 조회 (이메일 기반 매칭/연결에 필요)
    Optional<User> findByEmail(String email);

    boolean existsByProvider()

    User linkGoogleIfNeeded(Long userId);
    User createsocialUser(String email, String name, AuthProvider provider, String providerId);
}
