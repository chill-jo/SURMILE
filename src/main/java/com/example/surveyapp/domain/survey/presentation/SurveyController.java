package com.example.surveyapp.domain.survey.presentation;

import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyStatusUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.SurveyUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.*;
import com.example.surveyapp.domain.survey.application.SurveyService;
import com.example.surveyapp.domain.survey.presentation.dto.response.SurveyQuestionDto;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyController {

    private final SurveyService surveyService;

    //설문 생성
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<SurveyResponseDto> createSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid SurveyCreateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        SurveyResponseDto responseDto = surveyService.createSurvey(userId, requestDto);

        URI location = URI.create("/api/survey/" + responseDto.getId());
        return ResponseEntity
                .accepted()
                .location(location)
                .body(responseDto);
    }

    //설문 목록 조회(정렬 없이 삭제된 설문만 제외)
    @GetMapping
    public ResponseEntity<PageSurveyResponseDto<SurveyResponseDto>> getSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        PageSurveyResponseDto<SurveyResponseDto> pagedSurveys = surveyService.getSurveys(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(pagedSurveys);
    }

    //설문 상세정보 수정
    @PatchMapping("/{surveyId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<SurveyResponseDto> updateSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyUpdateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        SurveyResponseDto responseDto = surveyService.updateSurveyInfo(userId, surveyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //설문 상태 변경(NOT_STARTED -> IN_PROGRESS, IN_PROGRESS -> PAUSED, PAUSED -> IN_PROGRESS, IN_PROGRESS -> DONE)
    @PatchMapping("/{surveyId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<SurveyStatusResponseDto> updateSurveyStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyStatusUpdateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        SurveyStatusResponseDto responseDto = surveyService.updateSurveyStatus(userId, surveyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //설문 삭제
    @DeleteMapping("/{surveyId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<Void> deleteSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId
    ){
        Long userId = userDetails.getId();
        surveyService.deleteSurvey(userId, surveyId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 참여자 API
    // 설문 상세 조회
    @PreAuthorize("hasAnyRole('SURVEYEE', 'SURVEYOR')")
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyResponseDto> getSurvey(@PathVariable Long surveyId) {

        return ResponseEntity.status(HttpStatus.OK).body(surveyService.getSurvey(surveyId));
    }

    // 설문 시작
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @GetMapping("/{surveyId}/start")
    public ResponseEntity<SurveyQuestionDto> startSurvey(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long surveyId
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(surveyService.startSurvey(user.getId(), surveyId));
    }

    // 설문 시작 테스트
    @GetMapping("/{surveyId}/start/test")
    public ResponseEntity<SurveyQuestionDto> testStartSurvey(
            @PathVariable Long surveyId
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(surveyService.testStartSurvey(surveyId));
    }

}
