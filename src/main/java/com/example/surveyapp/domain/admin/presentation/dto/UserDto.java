package com.example.surveyapp.domain.admin.presentation.dto;

import com.example.surveyapp.domain.user.domain.model.User;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserDto {
    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final UserRoleEnum userRole;
    private final Boolean isDeleted;

    // JPA 프로젝션을 위한 생성자
    public UserDto(Long id, String email, String name, String nickname, UserRoleEnum userRole, Boolean isDeleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
        this.isDeleted = isDeleted;
    }

    // Entity -> DTO 변환 메서드
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getUserRole(),
                user.isDeleted()
        );
    }
}
