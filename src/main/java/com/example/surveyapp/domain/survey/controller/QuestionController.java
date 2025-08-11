package com.example.surveyapp.domain.survey.controller;

import com.example.surveyapp.domain.survey.controller.dto.request.QuestionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.QuestionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.PageQuestionResponseDto;
import com.example.surveyapp.domain.survey.controller.dto.response.QuestionResponseDto;
import com.example.surveyapp.domain.survey.application.QuestionService;
import com.example.surveyapp.global.response.ApiResponse;
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
public class QuestionController {

    private final QuestionService questionService;

    //***인증인가 추가 후 userId 부분 수정***
    @PostMapping("/{surveyId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> createQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @Valid @RequestBody QuestionCreateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        QuestionResponseDto responseDto = questionService.createQuestion(userId, surveyId, requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "질문이 생성되었습니다.", responseDto));
    }

    //질문 목록 조회
    @GetMapping("/{surveyId}/question")
    public ResponseEntity<ApiResponse<PageQuestionResponseDto<QuestionResponseDto>>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId
    ){
        Long userId = userDetails.getId();
        PageQuestionResponseDto<QuestionResponseDto> responseDto = questionService.getQuestions(page, size, userId, surveyId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "질문 목록을 조회했습니다.", responseDto));
    }

    //질문 단건 조회
    @GetMapping("/{surveyId}/question/{questionId}")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> getQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId
    ){
        Long userId = userDetails.getId();
        QuestionResponseDto responseDto = questionService.getQuestion(userId, surveyId, questionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "질문을 조회했습니다.", responseDto));
    }

    //***인증인가 추가 후 userId 부분 수정***
    @PatchMapping("/{surveyId}/question/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<QuestionResponseDto>> updateQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionUpdateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        QuestionResponseDto responseDto = questionService.updateQuestion(userId, surveyId, questionId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "질문이 수정되었습니다.", responseDto));
    }

    //***인증인가 추가 후 userId 부분 수정***
    @DeleteMapping("/{surveyId}/question/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId
    ){
        Long userId = userDetails.getId();
        questionService.deleteQuestion(userId, surveyId, questionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "질문이 삭제되었습니다.", null));
    }
}
