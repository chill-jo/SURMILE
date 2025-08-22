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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(req);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2 attributes: {}", attributes);

        String provider = req.getClientRegistration().getRegistrationId(); // "google", "kakao" ...

        OAuth2UserInfo info = switch (provider) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao"  -> new KakaoUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        };

        String providerId = info.getProviderId();
        String email      = info.getEmail();
        String name       = Optional.ofNullable(info.getName()).orElse("User");
//        String loginKey   = provider + "_" + providerId;

        // 1) provider + providerId로 조회 (가장 정확)
        Optional<User> existing = userRepository.findByProviderAndProviderId(provider, providerId);

        User user = existing.orElseGet(() -> {
            // 2) 첫 로그인이면 자동 회원가입
            String nickname = buildNickname(name, providerId);
            User created = User.of(
                    email,
                    null,                 // 소셜 로그인은 비번 null 가능
                    name,
                    nickname,
                    UserRoleEnum.SURVEYEE,
                    provider,
                    providerId
            );
            return userRepository.save(created);
        });

        return new CustomOauth2UserDetails(user, attributes);
    }

    private String buildNickname(String name, String providerId) {
        String base = (name == null || name.isBlank()) ? "user" : name.replaceAll("\\s+", "");
        String suffix = providerId != null && providerId.length() >= 6 ? providerId.substring(0, 6) : "000000";
        return (base + "_" + suffix).toLowerCase();
    }
}