package com.example.surveyapp.global;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@DisplayName("security:JWT")
@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

	@Autowired
	private JwtUtil jwtUtil;

	@Test
	public void jjwtCreateTokenTest() {
		String jwt = Jwts.builder()
			.subject("1L")
			.claim("role", "USER")
			.issuedAt(new Date())
			.expiration(new Date())
			.compact();
		System.out.println(jwt);
	}

	@Test
	@DisplayName("액세스 토큰을 생성한다.")
	public void createAccessToken() {
		// Given
		Long userId = 1L;
		UserRoleEnum role = UserRoleEnum.SURVEYEE;

		// When
		String jwt = jwtUtil.createAccessToken(userId, role);

		// Then
		System.out.println("jwt : " + jwt);
	}

	@Test
	@DisplayName("토큰을 검증한다.")
	public void verifyToken() throws InterruptedException {
		Long userId = 1L;
		UserRoleEnum role = UserRoleEnum.SURVEYEE;
		String accessToken = jwtUtil.createAccessToken(userId, role);
		String jwt = jwtUtil.substringToken(accessToken);

		// When
		Claims claims = jwtUtil.extractAllClaims(jwt);
		Assertions.assertThat(claims.getSubject()).isEqualTo(String.valueOf(userId));
		Assertions.assertThat(claims.get("role")).isEqualTo(role.name());
	}

	@Test
	@DisplayName("리프레시 토큰을 생성한다.")
	public void createRefreshToken() {
		// Given
		Long userId = 1L;

		// When
		String jwt = jwtUtil.createRefreshToken(userId);

		// Then
		Assertions.assertThat(jwtUtil.substringToken(jwt)).isNotEmpty();
	}
}
