//package com.example.surveyapp.global.security.handler;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import com.example.surveyapp.domain.user.domain.model.User;
//
//
//
//import com.example.surveyapp.global.security.auth.CustomOauth2UserDetails;
//import com.example.surveyapp.global.security.jwt.JwtUtil;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
//
//	private final JwtUtil jwtUtil;
//
//
//private String successRedirectUri = "http://localhost:8080/";
////private String successRedirectUri = "https://surmile.o-r.kr";
//
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//
//		CustomOauth2UserDetails principal = (CustomOauth2UserDetails)authentication.getPrincipal();
//		User user = principal.getUser();
//
//		// 1) JWT 발급 (UserRoleEnum 그대로 넘김)
//		String rawAccessToken = jwtUtil.createAccessToken(user.getId(), user.getUserRole());
//		String rawRefreshToken = jwtUtil.createRefreshToken(user.getId());
//
//		// 2) 프론트로 넘길 땐 "Bearer " 제거
//		String accessToken = jwtUtil.substringToken(rawAccessToken);
//		String refreshToken = jwtUtil.substringToken(rawRefreshToken);
//
//		// 3) 리다이렉트 (프론트에서 localStorage 등 보관 후 Authorization 헤더로 사용)
//		String redirect = successRedirectUri
//			+ "?at=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
//			+ "&rt=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
//			+ "&nickname=" + URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8);
//
//		log.info("OAuth2 success: userId={}, email={}, redirect={}", user.getId(), user.getEmail(), successRedirectUri);
//		response.sendRedirect(redirect);
//	}
//}

