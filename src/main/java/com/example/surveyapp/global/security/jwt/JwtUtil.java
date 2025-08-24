package com.example.surveyapp.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.redis.infrastructure.RedisTemplate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final RedisTemplate redisTemplate;

	private static final String BEARER_PREFIX = "Bearer ";
	//    private static final long TOKEN_TIME = 30 * 60 * 1000L; // 30분
	private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token_user_id_";
	private static final String EXPIRED_TOKEN_KEY_PREFIX = "expired_token_user_id_";

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Value("${spring.data.redis.cache-access-token}")
	private String ACCESS_TOKEN;
	@Value("${jwt.access-token.expiration.access-token}")
	private long ACCESS_TOKEN_TIME;

	@Value("${spring.data.redis.cache-refresh-token}")
	private String REFRESH_TOKEN;
	@Value("${jwt.access-token.expiration.refresh-token}")
	private long REFRESH_TOKEN_TIME;

	private SecretKey key;

	@PostConstruct
	public void init() {
		byte[] stringToByte = secretKey.getBytes(StandardCharsets.UTF_8);
		byte[] bytes = Base64.getEncoder().encode(stringToByte);
		key = Keys.hmacShaKeyFor(bytes);
	}

	/**
	 * JWT를 이용한 입장권(요청 권한)을 생성하기 위해 jjwt-0.12.5 라이브러리를 이용
	 * Jwts.builder().compact를 이용해서 JWT(Json Web Token)
	 * setSubject(), claim, issuedAt, expiration을 이용해서 jwt 발급에 필요한 정보를 설정할 수 있다.
	 *  - setsubject : 발급 주체(회원 번호)
	 *  - claim : jwt에 대한 추가 정보(회원 권한)
	 *  - issuedAt: 토큰 발급 시간(현재 시간)
	 *  - expiration: 토큰 만료 시간(발급 시간으로 부터 30분)
	 * 해당 jwt를 만들때, 외부에서 생성이 불가능하도록, 반드시 우리시스템에서만 만들수 있도록 하는 장치 = key
	 * - signWith: 우리만 생성할 수 있는 유일한 key
	 */
	public String createAccessToken(Long userId, UserRoleEnum userRole) {
		Date now = new Date();

		return BEARER_PREFIX + Jwts.builder()
			.setSubject(String.valueOf(userId)) // id : 1
			.claim("role", userRole.name()) // role: SURVEYEE
			.issuedAt(now) // 2025-07-26T16:21:47
			.expiration(new Date(now.getTime() + ACCESS_TOKEN_TIME)) // 발급일로부터 30분
			// 다른 시스템에서 우리시스템에 들어오기 위해 필요한 입장권을 만들지 못하도록 하는 장치
			.signWith(key)
			.compact();
	}

	// Refresh Token 발급
	public String createRefreshToken(Long userId) {
		Date now = new Date();

		return BEARER_PREFIX + Jwts.builder()
			.setSubject(String.valueOf(userId)) // id : 1
			.issuedAt(now) // 2025-07-26T16:21:47
			.expiration(new Date(now.getTime() + REFRESH_TOKEN_TIME)) // 발급일로부터 14일
			.signWith(key)
			.compact();

	}

	// Refresh Token 유효 검증
	public boolean isValidRefreshToken(String refreshToken) {

		Long userId = Long.parseLong(extractUserId(refreshToken));
		return refreshToken.equals(redisTemplate.read(REFRESH_TOKEN + ":" + userId, String.class));

	}

	/**
	 * 헤더에서 "Bearer <토큰>" 형식에서 토큰만 추출
	 */

	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		throw new IllegalStateException("JWT 토큰이 필요합니다.");
	}

	/**
	 * JWT 토큰에서 모든 클레임을 추출합니다.
	 * - jjwt의 Jwts.parser() 메서드를 호출하면 String jwt의 내부 값을 추출할 수 있음
	 * - Jwts.parser()를 호출하기 위해서는 생성한 key와 동일한 key로 해석이 가능해야함
	 *   - verifyWith: 생성에 사용했던 동일한 key를 설정
	 * - Jwts. parser 내부의 메서드를 이용해서 발급 주체인 subject 혹은 jwt의 추가 정보가 구성된 claims의 값에 접근이 가능
	 *   - parseSignedClaims() -> claims에 접근
	 *   - getSubject() -> 발급 주체인 subject 값에 접근
	 */
	public Claims extractAllClaims(String token) {
		JwtParser parser = Jwts.parser()
			.verifyWith(key)
			.build();

		return parser
			.parseSignedClaims(token)
			.getPayload();
	}

	/**
	 * 토큰 자체의 유효성 검증 메서드
	 */

	public boolean validateToken(String token) {

		Long userId = Long.parseLong(extractUserId(token));
		String expiredToken = redisTemplate.read(ACCESS_TOKEN + ":" + userId, String.class);
		if (expiredToken != null && expiredToken.equals(token)) {
			return false;
		}

		try {
			Claims claims = extractAllClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public String extractUserId(String token) {
		return extractAllClaims(token).getSubject();
	}
}
