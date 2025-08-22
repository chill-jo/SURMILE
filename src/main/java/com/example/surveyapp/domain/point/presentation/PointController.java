package com.example.surveyapp.domain.point.presentation;

import com.example.surveyapp.domain.point.presentation.dto.request.PointChargeRequestDto;
import com.example.surveyapp.domain.point.presentation.dto.response.PointHistoryResponseDto;
import com.example.surveyapp.domain.point.application.PointService;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomSecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    //포인트 충전
    @PreAuthorize("hasAnyRole('SURVEYOR')")
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<Void>> charge(
            @Valid @RequestBody PointChargeRequestDto dto,
            @AuthenticationPrincipal CustomSecurityUserDetails userDetails
    ){
        pointService.charge(userDetails.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("결제가 되었습니다.",null));
    }

    //포인트 조회
    @PreAuthorize("hasAnyRole('SURVEYOR', 'SURVEYEE')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PointHistoryResponseDto>>> getHistories(
            @AuthenticationPrincipal CustomSecurityUserDetails userDetails,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ){
        Page<PointHistoryResponseDto> page = pointService.getHistories(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("포인트 내역이 조회되었습니다.",page));
    }
}