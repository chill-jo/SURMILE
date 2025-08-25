package com.example.surveyapp.global.security.oauth;

public interface OAuth2UserInfo {
	String getProvider();     // "google", "kakao"

	String getProviderId();   // 각 provider가 주는 고유 ID

	String getEmail();

	String getName();
}
