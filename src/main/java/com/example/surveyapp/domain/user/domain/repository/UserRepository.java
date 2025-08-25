package com.example.surveyapp.domain.user.domain.repository;

import java.util.Optional;

import jakarta.persistence.Column;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapp.domain.admin.presentation.dto.UserDto;
import com.example.surveyapp.domain.user.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndIsDeletedFalse(String email);

	Optional<User> findByIdAndIsDeletedFalse(Long userId);

	boolean existsByIdAndIsDeletedFalse(Long userId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	@Query(value = """
		    SELECT new com.example.surveyapp.domain.admin.presentation.dto.UserDto(
		                  u.id,
		                  u.email,
		                  u.name,
		                  u.nickname,
		                  u.userRole,
		                  u.isDeleted
		              )
		    FROM User u
		    WHERE (:searchText IS NULL 
		           OR u.email LIKE CONCAT('%', :searchText, '%')
		           OR u.name LIKE CONCAT('%', :searchText, '%')
		           OR u.nickname LIKE CONCAT('%', :searchText, '%'))
		""")
	Page<UserDto> findAllBySearch(@Param("searchText") String search, Pageable pageable);

//	// ===== 소셜 로그인용 =====
//	// provider + providerId로 활성 사용자 조회 (구글/카카오 1차 키)
//	Optional<User> findByProviderAndProviderIdAndIsDeletedFalse(String provider, String providerId);
//
//	// provider + providerId 존재 여부 (중복 가입 방지)
//	boolean existsByProviderAndProviderIdAndIsDeletedFalse(String provider, String providerId);
//
//	// 필요 시 이메일 기반 조회(활성만) — 기존 메서드로 대체 가능하지만 명시적으로 ㅇ일단 둠
//	Optional<User> findByEmail(String email); // 폼 로그인/이메일 링크드 계정 처리 시 유용
}
