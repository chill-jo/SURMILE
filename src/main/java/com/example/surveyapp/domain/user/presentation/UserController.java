package com.example.surveyapp.domain.user.presentation;

import com.example.surveyapp.domain.user.application.provider.JwtProvider;
import com.example.surveyapp.domain.user.presentation.dto.*;
import com.example.surveyapp.domain.user.presentation.dto.RegisterRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserRequestDto;
import com.example.surveyapp.domain.user.presentation.dto.UserResponseDto;
import com.example.surveyapp.domain.user.application.UserService;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import com.example.surveyapp.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    // 회원 정보 조회
    @GetMapping("/my-page")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info(String.valueOf(userDetails.getId()));
        UserResponseDto getResponseDto = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(getResponseDto);
    }

    // 회원 정보 수정
    @PatchMapping("/my-page")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        log.info(String.valueOf(userDetails.getId()));
        UserResponseDto updatedResponseDto = userService.updateMyInfo(userDetails.getId(), requestDto);
        return ResponseEntity.ok(updatedResponseDto);
    }

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestDto requestDto
    ) {
        userService.register(requestDto);
        return ResponseEntity.ok(null);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto requestDto
    ) {
        LoginResponseDto response = userService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userService.logout(accessToken);
        return ResponseEntity.ok(null);
    }

    //토큰재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestHeader("Authorization") String refreshToken
    ) {
        LoginResponseDto response = userService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }

    //회원탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 로그인 유저 정보
            @RequestBody @Valid WithdrawRequestDto requestDto
    ) {
        userService.withdraw(userDetails.getId(), requestDto);
        return ResponseEntity.ok(null);
    }


    // 참여자 기초 정보 등록 선택지 보기
    @PreAuthorize("hasRole('SURVEYEE')")
    @GetMapping("/surveyee/base-data-info")
    public ResponseEntity<BaseDataInfoResponseDto> getBaseDataInfo() {
        return ResponseEntity.ok(userService.getBaseDataInfo());
    }

    // 참여자 기초 정보 C,U
    @PreAuthorize("hasRole('SURVEYEE')")
    @PostMapping("/surveyee/base-datas")
    public ResponseEntity<Void> saveBaseDatas(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody BaseDataListRequestDto requestDto
    ) {
        userService.saveBaseDatas(user.getId(), requestDto);
        return ResponseEntity.ok(null);
    }

    // 참여자 기초 정보 R
    @PreAuthorize("hasRole('SURVEYEE')")
    @GetMapping("/surveyee/base-datas")
    public ResponseEntity<BaseDataListResponseDto> getBaseDatas( @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userService.getBaseDatas(user.getId()));
    }

}
