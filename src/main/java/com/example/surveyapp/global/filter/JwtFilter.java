package com.example.surveyapp.global.filter;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.repository.UserRepository;
import com.example.surveyapp.global.response.exception.UnauthorizedException;
import com.example.surveyapp.global.security.jwt.CustomSecurityUserDetails;
import com.example.surveyapp.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     *
     * 1. Authorization Header가 있는지 검증(해당 요청이 인증 항목을 포함하고 있는지)
     * 2. Authorization Header가 Bearer 타입인지 검증(JWT 기반 인증 방식인지)
     * 3. 우리가 발급한 입장권인지
     *     - Jwt를 만들때 사용했던 sercret-key를 기반으로 해석했을 때, 해석이 된다면 우리가 발급한 JWT 구나
     * 4. 우리가 발급한 입정권이라면 유효기간은 지나지 않았는지
     * 5. 우리가 발급한 입장권이고, 유효기간이 지나지 않은 사용가능한 입장권(jwt)라면 입장권 검사(필터링) 이후 비지니스 로직 혹은 규칙등을 실행하는데 필요한 인증된 사용자 정보를 제공하기 위해서 SecurityContextHolder라는 공간에 jwt로 부터 추출한 인증된 사용자 정보(CusomUserDetilas)를 저장
     *     - 인증된 사용자 정보가 필요한 비지니스 로직 처리 부는 @AuthenticationPrincipal과 같은 어노테이션을 이용해서 SecurityContextHolder에 접근이 가능
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Authorization Header가 있는지 검증(해당 요청이 인증 항목을 포함하고 있는지)

        String path = request.getRequestURI();

        if (path.equals("/api/register") || path.equals("/api/login")) {
            filterChain.doFilter(request, response); // 토큰 검증 없이 통과
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        // 2. Authorization Header가 Bearer 타입인지 검증(JWT 기반 인증 방식인지)
        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = jwtUtil.substringToken(bearerJwt);

        // 3. 우리가 발급한 입장권인지 유효한 입장권인지
        // - Jwt를 만들때 사용했던 sercret-key를 기반으로 해석했을 때, 해석이 된다면 우리가 발급한 JWT 구나
        // - 유효 기간, JWT의 형식 검증등을 포함
        if (!path.equals("/api/refresh")) {
            if (!jwtUtil.validateToken(jwt)) {
                throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
            }

            if (jwtUtil.extractAllClaims(jwt).get("role") == null) {
                throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
            }
        }


        // 5. 우리가 발급한 입장권이고, 유효기간이 지나지 않은 사용가능한 입장권(jwt)라면 입장권 검사(필터링) 이후
        // 비지니스 로직 혹은 규칙등을 실행하는데 필요한 인증된 사용자 정보를 제공하기 위해
        // SecurityContextHolder라는 공간에 jwt로 부터 추출한 인증된 사용자 정보(CusomUserDetilas)를 저장
        // - 인증된 사용자 정보가 필요한 비지니스 로직 처리 부는
        // @AuthenticationPrincipal과 같은 어노테이션을 이용해서 SecurityContextHolder에 접근이 가능
        String subject = jwtUtil.extractUserId(jwt);
        Long userId = Long.parseLong(subject);

        // SecurityContextHolder라는 공간에 jwt로 부터 추출한 인증된 사용자 정보(CusomUserDetilas)를 저장하기 위해
        // 1. 회원 테이블에서 인증된 사용자 정보를 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("해당 유저가 없습니다."));

        // 2. 인증된 사용자 정보를 인증된 사용자 정보가 필요한 비지니스 로직 처리 부에 접근 가능하도록 하기 위해
        // SecurityContextHolder에 저장하기 위한 타입으로 변환: CustomUserDetails, UsernamePasswordAuthenticationToken
        CustomSecurityUserDetails customSecurityUserDetails = new CustomSecurityUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                customSecurityUserDetails, null, customSecurityUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
