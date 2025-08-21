package com.example.surveyapp.global.security.handler;

import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.domain.model.AuthProvider;
import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.exception.UserErrorCode;
import com.example.surveyapp.domain.user.exception.UserException;
import com.example.surveyapp.global.store.OauthUserStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService clientService;
    private final JwtProvider jwtProvider;

    private final OauthUserStore oauthUserStore;
    private final UserService userService;

    // 프론트로 리다이렉트할 URL (yml에서 주입)
    @Value("${app.oauth2.redirect-uri:https://localhost:3000/oauth2/success}")
    private String redirectUrl;

    // 쿠키 옵션
    @Value("${app.security.cookie.domain:}")
    private String cookieDomain; // 필요없으면 빈값

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // "google", "kakao", ...
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // 1) 구글/카카오 등 외부 토큰 꺼내기
        OAuth2AuthorizedClient client =
                clientService.loadAuthorizedClient(registrationId, authentication.getName());
        String accessTokenFromProvider = client != null && client.getAccessToken() != null
                ? client.getAccessToken().getTokenValue() : null;

        String idTokenFromProvider = null;
        if (principal instanceof OidcUser oidc) { // 구글 OIDC라면 id_token 존재
            idTokenFromProvider = oidc.getIdToken().getTokenValue();
        }

        // 2) provider / providerId(sub) / email / name 추출
        AuthProvider provider = mapProvider(registrationId);
        ProviderProfile profile = extractProviderProfile(provider, principal);

        // 3) 우리 DB 매칭: (provider, providerId) 우선
        //Boolean isUserGoogleEmail = userReader.validateUserExistsByProvider(provider, providerId);

        final boolean existsByProvider = oauthUserStore.existsByProvider(provider, providerId).

        Boolean isUserExistsByProvider = oauthUserStore.existsByProvider(provider, profile.providerId()))
        if(!isUserExistsByProvider){
            Boolean isUserExistsByEmail = oauthUserStore.existsByEmail(email);
        }

        //OAuthUserDto 에는 userId랑 userRole을 담아서 준다.?!?!
        if(isUserExistsByEmail){
            OAuthUserDto userDto = oauthUserStore.linkByEmailIfUnlinked(profile.email(), provider, profile.providerId());
        }
        else{
            OAuthUserDto userDto = oauthUserStore.createSocialUser(profile.email(), profile.name(), provider, profile.providerId());
        }


        public OAuthUserDto resolveOrCreateOpt(AuthProvider provider, ProviderProfile p)
        User user = oauthUserStore.findByProvider(provider, profile.providerId()).orElse(()->
                Optional.ofNullable(profile.email()))
                    .flatMap(oauthUserStore::findByEmail)
                    .map(u->oauthUserStore.linkByEmailIflinked(p.email(),p.providerId()))
                    .orElseGet(()-> oauthUserStore.createsocialUser(p.email(), p.name(), provider, p.providerId()))
                );
        return OAuthUserDto.of(user.getId(), user.getRole());


        oauthUserStore.existsByProvider(provider, profile.providerId())
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER));

        Optional<User> byProvider = oauthUserStore.findByProvider(provider, profile.providerId());

        User user = byProvider.orElseGet(() -> {
            // 이메일로 기존 계정 연결 정책이 있으면 여기서 처리
            Optional<User> byEmail = profile.email() != null
                    ? oauthUserStore.findByEmail(profile.email()) : Optional.empty();
            if (byEmail.isPresent()) {
                // 같은 이메일 계정에 소셜 연결
                return oauthUserStore.linkByEmailIfUnlinked(profile.email(), provider, profile.providerId());
            }
            // 신규 소셜 유저 생성
            return oauthUserStore.createSocialUser(profile.email(), profile.name(), provider, profile.providerId());
        });

        // 4) 우리 서비스용 JWT 발급
        String accessToken = jwtProvider.createAccessToken(userDto.getId(), userDto.getRole()); // UserRoleEnum
        String refreshToken = jwtProvider.createRefreshToken(userDto.getId());

        // 5) 쿠키에 우리 토큰 세팅 (또는 헤더/쿼리로?)
        addCookie(res, "ACCESS_TOKEN", accessToken, /*maxAgeSec*/ 60 * 60, true);
        addCookie(res, "REFRESH_TOKEN", refreshToken, /*maxAgeSec*/ 60 * 60 * 24 * 14, true);

        // 6) 프론트로 리다이렉트
        String safeName = URLEncoder.encode(Optional.ofNullable(profile.name()).orElse(""), StandardCharsets.UTF_8);
        String location = redirectUrl + "?userId=" + userDto.getId() + "&name=" + safeName;

        // 디버그용 로그
        log.info("OAuth2 success: provider={}, providerId={}, email={}, userId={}, hasGoogleIdToken={}",
                provider, profile.providerId(), profile.email(), userDto.getId(), idTokenFromProvider != null);

        res.sendRedirect(location);
    }

    /* ---------- helpers ---------- */

    private AuthProvider mapProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "kakao"  -> AuthProvider.KAKAO;
            default       -> AuthProvider.LOCAL;
        };
    }

    private ProviderProfile extractProviderProfile(AuthProvider provider, OAuth2User principal) {
        if (principal instanceof OidcUser oidc && provider == AuthProvider.GOOGLE) {
            // 구글 OIDC: sub, email, name 모두 안정적으로 제공
            String sub = oidc.getSubject();
            String email = oidc.getEmail();
            String name = oidc.getFullName(); // 없으면 claim "name"
            if (name == null) name = oidc.getClaimAsString("name");
            return new ProviderProfile(sub, email, name);
        }

        if (provider == AuthProvider.KAKAO) {
            // 카카오: id 최상위, email은 동의 안 하면 null
            Map<String, Object> a = principal.getAttributes();
            String id = String.valueOf(a.get("id"));
            String email = null;
            String name = null;

            Object kakaoAccount = a.get("kakao_account");
            if (kakaoAccount instanceof Map<?, ?> ka) {
                Object emailObj = ka.get("email");
                if (emailObj != null) email = String.valueOf(emailObj);

                Object profileObj = ka.get("profile");
                if (profileObj instanceof Map<?, ?> p) {
                    Object nickname = p.get("nickname");
                    if (nickname != null) name = String.valueOf(nickname);
                }
            }
            return new ProviderProfile(id, email, name);
        }

        // 기타 공급자(네이버 등) 필요 시 분기 추가
        // 기본 폴백
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String id = String.valueOf(principal.getName());
        return new ProviderProfile(id, email, name);
    }

    private void addCookie(HttpServletResponse res, String name, String value, int maxAgeSec, boolean httpOnly) {
        Cookie c = new Cookie(name, value);
        c.setPath("/");
        c.setMaxAge(maxAgeSec);
        c.setHttpOnly(httpOnly);
        // 필요한 경우 도메인/secure/samesite 설정
        if (!cookieDomain.isBlank()) c.setDomain(cookieDomain);
        c.setSecure(true); // HTTPS 권장
        // SameSite=None; Secure 를 써야 크로스 도메인에서 브라우저가 쿠키 보냄
        res.addHeader("Set-Cookie",
                "%s=%s; Max-Age=%d; Path=/; %s%s; SameSite=None"
                        .formatted(name, value, maxAgeSec,
                                httpOnly ? "HttpOnly; " : "",
                                c.getDomain() != null ? ("Domain=" + c.getDomain() + "; ") : "Secure"));
    }

    private record ProviderProfile(String providerId, String email, String name) {}
}
