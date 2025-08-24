//package com.example.surveyapp.global.security.auth;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Map;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import com.example.surveyapp.domain.user.domain.model.User;
//
//public class CustomOauth2UserDetails implements UserDetails, OAuth2User {
//
//	// 우리 DB User 엔티티
//	private final User user;
//
//	// 구글/카카오 등 OAuth2 provider에서 내려주는 유저 정보들
//	private Map<String, Object> attributes;
//
//	// 생성자 (User 엔티티 + 소셜 로그인 attributes 같이 보관)
//	public CustomOauth2UserDetails(User user, Map<String, Object> attributes) {
//		this.user = user;
//		this.attributes = attributes;
//	}
//
//	// ===================== OAuth2User 구현부 =====================
//
//	// 소셜 로그인에서 내려준 raw attributes 반환 (이메일, 이름, picture 등)
//	@Override
//	public Map<String, Object> getAttributes() {
//		return attributes;
//	}
//
//	// 소셜 로그인 식별자 (구글은 "sub", 카카오는 "id") 여기선 DB에 저장한 providerId 반환
//	@Override
//	public String getName() {
//		return user.getProviderId();
//	}
//
//	// ===================== UserDetails 구현부 =====================
//
//	// 권한 정보 반환 (Spring Security에서 ROLE 체크할 때 씀)
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		Collection<GrantedAuthority> collection = new ArrayList<>();
//		// User 엔티티의 userRole (ADMIN, SURVEYOR, SURVEYEE) 가져와서 Spring Security 형식으로 감쌈
//		System.out.println("user.getUserRole().name() : " + user.getUserRole().name());
//		collection.add(() -> user.getUserRole().name());
//		return collection;
//	}
//
//	// 비밀번호 (폼 로그인에서만 사용, 소셜 로그인은 null일 수도 있음)
//	@Override
//	public String getPassword() {
//		return user.getPassword();
//	}
//
//	// 로그인 아이디로 email 사용 (Spring Security 기본 username 자리)
//	@Override
//	public String getUsername() {
//		return user.getEmail();
//	}
//
//	// 계정 만료 여부 (true면 만료 안 됨)
//	@Override
//	public boolean isAccountNonExpired() {
//		return true;
//	}
//
//	// 계정 잠김 여부 (true면 잠김 아님)
//	@Override
//	public boolean isAccountNonLocked() {
//		return true;
//	}
//
//	// 비밀번호 만료 여부 (true면 만료 아님)
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return true;
//	}
//
//	// 계정 활성화 여부 (true면 활성화됨)
//	// 여기선 User 엔티티의 isDeleted 값 반영 → 탈퇴 유저는 비활성화
//	@Override
//	public boolean isEnabled() {
//		return !user.isDeleted();
//	}
//
//	// DB User 엔티티 직접 꺼내쓰고 싶을 때
//	public User getUser() {
//		return user;
//	}
//}
