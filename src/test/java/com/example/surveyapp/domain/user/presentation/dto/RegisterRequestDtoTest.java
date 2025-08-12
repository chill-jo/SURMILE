package com.example.surveyapp.domain.user.presentation.dto;


import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterRequestDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 회원_역할이_SURVEYEE_또는_SURVEYOR_이면_성공() {
        RegisterRequestDto dto = new RegisterRequestDto(
                "test@example.com",
                "Password1!",
                "Password1!",
                "Jihyun",
                "ji",
                UserRoleEnum.SURVEYEE // 또는 SURVEYOR
        );

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void 회원_역할이_ADMIN이면_유효성_검사_실패() {
        RegisterRequestDto dto = new RegisterRequestDto(
                "test@example.com",
                "Password1!",
                "Password1!",
                "Jihyun",
                "ji",
                UserRoleEnum.ADMIN
        );

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getMessage().equals("회원 유형은 SURVEYEE 또는 SURVEYOR만 가능합니다."));
    }
}
