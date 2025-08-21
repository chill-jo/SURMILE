package com.example.surveyapp.global.config;

import com.example.surveyapp.domain.admin.application.facade.UserFacade;
import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.filter.JwtFilter;
import com.example.surveyapp.global.oauth.reader.OauthReader;
import com.example.surveyapp.global.security.handler.CustomAccessDeniedHandler;
import com.example.surveyapp.global.security.handler.CustomAuthenticationEntryPoint;
import com.example.surveyapp.global.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserFacade userFacade;

    // 기존 서비스/유틸 주입만 추가
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OauthReader oauthReader;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 비회원 허용
                        .requestMatchers(
                                "/api/register",
                                "/api/login",
                                "/api/logout",
                                "/api/refresh",
                                "/error",
                                "/api/chat/ask",

                                // 구글 OAuth2 플로우 허용
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        // 관리자
                        .requestMatchers("/api/admin/**").hasRole(UserRoleEnum.ADMIN.getRole())
                        .requestMatchers("/api/chat/index").hasRole(UserRoleEnum.ADMIN.getRole())

                        // 유저 권한
                        .requestMatchers("/api/user/**").hasAnyRole(
                                UserRoleEnum.SURVEYEE.getRole(),
                                UserRoleEnum.SURVEYOR.getRole()
                        )

                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 커스텀 파일 없이 성공핸들러만 등록
                .oauth2Login(oauth -> oauth
                        .successHandler(this::googleSuccessHandler)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 새 파일 없이, 람다로 성공 핸들러 구현
    private void googleSuccessHandler(HttpServletRequest req,
                                      HttpServletResponse res,
                                      Authentication auth)  {

        OAuth2User o = (OAuth2User) auth.getPrincipal();
        // DefaultOAuth2UserService 기본 사용 → 구글 키 그대로 옴
        String email = (String) o.getAttributes().get("email");
        String sub   = (String) o.getAttributes().get("sub");
        String name  = (String) o.getAttributes().get("name");

        oauthReader.validateUserEmailOrThrow(email);


        // 1) 이메일로 기존 사용자 조회
//        Optional<User> opt = userRepository.findByEmailAndIsDeletedFalse(email);
//        if (opt.isPresent()) {
//            User user = opt.get();

            // LOCAL 계정이면 최초 한 번 구글 연동 허용
//            if (user.getProvider() == null || user.getProvider() == AuthProvider.LOCAL) {
//                user.linkSocial(AuthProvider.GOOGLE, sub);
//                userRepository.save(user);
//            }

            // JWT 발급 + 쿠키 세팅 후 성공 페이지로
//            String access  = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
//            String refresh = jwtProvider.createRefreshToken(user.getId());
//            res.addCookie(httpOnlyCookie("ACCESS_TOKEN", access));
//            res.addCookie(httpOnlyCookie("REFRESH_TOKEN", refresh));
//
//            res.sendRedirect("http://localhost:3000/login/success"); // 프론트 주소..?
//            return;
//        }

        // 2) 신규 유저면 기존 회원가입 화면으로 리다이렉트 (프리필)
//        String qs = "email=" + enc(email) +
//                "&name=" + enc(name) +
//                "&provider=GOOGLE&providerId=" + enc(sub);
//        res.sendRedirect("http://localhost:3000/register?" + qs);
//    }

    private Cookie httpOnlyCookie(String name, String value) {
        Cookie c = new Cookie(name, value);
        c.setPath("/");
        c.setHttpOnly(true);
        c.setSecure(true); // HTTPS에서 true 권장
        c.setMaxAge(60 * 60 * 24 * 7);
        return c;
    }

    private String enc(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }
}