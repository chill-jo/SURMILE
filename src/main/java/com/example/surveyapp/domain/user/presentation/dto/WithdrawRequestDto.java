package com.example.surveyapp.domain.user.presentation.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawRequestDto {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
