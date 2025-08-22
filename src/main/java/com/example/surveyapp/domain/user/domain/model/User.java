package com.example.surveyapp.domain.user.domain.model;

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

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, String name, String nickname, UserRoleEnum userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public static User of(String email, String password, String name, String nickname, UserRoleEnum role) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .userRole(role)
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
}
