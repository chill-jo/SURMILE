package com.example.surveyapp.domain.user.domain.model;

import com.example.surveyapp.domain.user.domain.model.AuthProvider;
import com.example.surveyapp.global.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    private boolean isDeleted = false;

    // 어떤 로그인 제공자인지
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;

    // 소셜 고유 식별자(구글의 sub 등). 소셜 미연동이면 null
    @Column(unique = true, length = 64)
    private String providerId;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, String name, String nickname,
                 UserRoleEnum userRole, AuthProvider provider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
        this.provider = provider == null ? AuthProvider.LOCAL : provider;
        this.providerId = providerId;
    }

    public static User of(String email, String password, String name, String nickname, UserRoleEnum role) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .userRole(role)
                .provider(AuthProvider.LOCAL)
                .build();
    }

    //admin 인프라 구축용
    public static User createAdmin(String adminEmail, String adminName, String adminNickname, String adminPassword) {
        return User.builder()
                .email(adminEmail)
                .name(adminName)
                .nickname(adminNickname)
                .password(adminPassword)
                .userRole(UserRoleEnum.ADMIN)
                .provider(AuthProvider.LOCAL)
                .build();
    }

    public void softDelete(){
        this.isDeleted = true;
    }

    public void updateInfo(String email, String name, String nickname, String rawPassword, PasswordEncoder passwordEncoder) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;

        if (rawPassword != null && !rawPassword.isBlank()) {
            this.password = passwordEncoder.encode(rawPassword);
        }
    }

    public boolean hasRole(UserRoleEnum userRole){
        return this.userRole.equals(userRole);
    }

    public boolean isUserRoleSurveyee(){
        return this.userRole.equals(UserRoleEnum.SURVEYEE);
    }
    public boolean isUserRoleNotAdmin(){
        return !this.userRole.equals(UserRoleEnum.ADMIN);
    }
    public boolean isUserRoleSurveyor(){
        return userRole.equals(UserRoleEnum.SURVEYOR);
    }

    // 소셜 연동(LOCAL → GOOGLE/KAKAO)
    public void linkSocial(AuthProvider provider, String providerId) {
        if (this.provider != AuthProvider.LOCAL && this.provider != provider) {
            throw new IllegalStateException("이미 다른 소셜로 연동된 계정입니다.");
        }
        this.provider = provider;
        this.providerId = providerId;
    }
}
