package com.example.surveyapp.global.config;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.filter.JwtFilter;
import com.example.surveyapp.global.security.handler.CustomAccessDeniedHandler;
import com.example.surveyapp.global.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // @Secured, @PreAuthorize 사용 가능
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 비회원 허용
                        .requestMatchers(
                                "/index.html",
                                "/api/register",
                                "/api/login",
                                "/api/logout",
                                "/api/refresh",
                                "/error",
                                "/api/chat/ask"
                        ).permitAll()

                        // 관리자 권한 필요
                        .requestMatchers("/api/admin/**").hasRole(UserRoleEnum.ADMIN.getRole())
                        .requestMatchers("/api/chat/index").hasRole(UserRoleEnum.ADMIN.getRole())

                        // 유저 권한 필요 (설문 참여자, 출제자 모두 허용)
                        .requestMatchers("/api/user/**").hasAnyRole(
                                UserRoleEnum.SURVEYEE.getRole(),
                                UserRoleEnum.SURVEYOR.getRole()
                        )

                        // 나머지 인증 필요
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)   // 401 인증 실패
                        .accessDeniedHandler(accessDeniedHandler)             // 403 인가 실패
                )


                //UsernamePasswordAuthenticationFilter : 스프링 시큐리티에서 기본적으로
                // username, password와 같은 입력값을 이용해서 인증을 시도하고, 인증된 사용자 정보를
                // UsernamePasswrodAuthenticationToken으로 구성해서 SecurityContextHodler에 저장하는
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)


                .build();
    }
}