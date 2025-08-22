package com.example.surveyapp.global.security.oauth;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.security.auth.CustomOauth2UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 로그인 성공 시 호출됨.
     * 1) Provider(google/kakao) 판별
     * 2) Provider별 attributes를 공통 인터페이스(OAuth2UserInfo)로 변환
     * 3) (provider, providerId)로 기존 유저 조회
     * 4) 없으면 첫 로그인이고 자동 회원가입
     * 5) Security Principal로 감싸서 반환
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        // 0) 기본 구현으로부터 사용자 정보(attributes) 로드
        OAuth2User oAuth2User = super.loadUser(req);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2 attributes: {}", attributes);

        // 1) 어떤 소셜인지 식별 (registrationId: "google", "kakao" ...)
        String provider = req.getClientRegistration().getRegistrationId();

        // 2) Provider별 파서로 통일된 DTO 생성
        OAuth2UserInfo info = switch (provider) {
            case "google" -> new GoogleUserInfo(attributes); // "sub", "email", "name"
            case "kakao"  -> new KakaoUserInfo(attributes);  // "id", "kakao_account.email", "profile.nickname"
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        };

        // 3) 표준화된 값 꺼내기
        String providerId = info.getProviderId();      // 구글: sub / 카카오: id
        String email      = info.getEmail();           // 카카오는 이메일 동의 안 하면 null 가능
        String name       = Optional.ofNullable(info.getName()).orElse("User");

        // 4) (provider, providerId) 기준으로 기존 유저 탐색 (소셜 계정의 1차 식별키)
        Optional<User> existing =
                userRepository.findByProviderAndProviderIdAndIsDeletedFalse(provider, providerId);

        // 5) 없으면 자동 회원가입 (소셜 전용)
        User user = existing.orElseGet(() -> {
            String nickname = buildNickname(name, providerId); // 닉네임 생성 규칙
            User created = User.ofSocial(
                    email,
                    name,
                    nickname,
                    UserRoleEnum.SURVEYEE,
                    provider,
                    providerId
            );
            return userRepository.save(created);
        });

        // 6) 스프링 시큐리티에서 사용할 Principal로 감싸서 반환
        return new CustomOauth2UserDetails(user, attributes);
    }

    /**
     * name + "_" + providerId 앞 6자리 조합으로 단순 닉네임 생성.
     * - name이 비어있으면 "user"로 대체
     * - providerId가 짧거나 null이면 "000000" 사용
     */
    private String buildNickname(String name, String providerId) {
        String base = (name == null || name.isBlank()) ? "user" : name.replaceAll("\\s+", "");
        String suffix = (providerId != null && providerId.length() >= 6)
                ? providerId.substring(0, 6)
                : "000000";
        return (base + "_" + suffix).toLowerCase();
    }
}