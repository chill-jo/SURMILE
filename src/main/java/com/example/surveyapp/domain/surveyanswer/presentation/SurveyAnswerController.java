package com.example.surveyapp.domain.surveyanswer.presentation;

import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerService;
import com.example.surveyapp.domain.surveyanswer.application.SurveyAnswerStatisticsService;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.request.SurveyAnswerRequestDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyStatisticsDto;
import com.example.surveyapp.domain.surveyanswer.presentation.dto.response.SurveyeeSurveyListDto;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyAnswerController {

    private final SurveyAnswerService surveyAnswerService;
    private final SurveyAnswerStatisticsService statisticsService;
    private final SurveyAnswerStatisticsService surveyAnswerStatisticsService;

    // 설문 응답 제출
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @PostMapping("/{surveyId}/answer")
    public ResponseEntity<Void> answerSurvey(
            @PathVariable Long surveyId,
            @RequestBody @Valid SurveyAnswerRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        surveyAnswerService.saveSurveyAnswer(surveyId, requestDto, user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 참여자 본인 설문 참여 내역 조회
    @PreAuthorize("hasAnyRole('SURVEYEE')")
    @GetMapping("/surveyee")
    public ResponseEntity<SurveyeeSurveyListDto> getUserSurveyAnswerHistory(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(surveyAnswerService.getUserSurveyAnswerHistory(user.getId()));
    }

    //설문 통계 조회
    @PreAuthorize("hasAnyRole('ADMIN', 'SURVEYOR')")
    @GetMapping("/{surveyId}/result")
    public ResponseEntity<SurveyStatisticsDto> getSurveyStatistics(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long surveyId
    ){
        SurveyStatisticsDto statisticsDto = surveyAnswerStatisticsService.getStatistics(surveyId);

        return ResponseEntity.status(HttpStatus.OK).body(statisticsDto);
    }

}
