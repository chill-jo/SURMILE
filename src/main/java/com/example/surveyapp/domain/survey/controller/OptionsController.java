package com.example.surveyapp.domain.survey.controller;

import com.example.surveyapp.domain.survey.controller.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.controller.dto.response.OptionResponseDto;
import com.example.surveyapp.domain.survey.application.OptionsService;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey/")
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping("/{surveyId}/question/{questionId}/option")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<OptionResponseDto>> createOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @RequestBody OptionCreateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        OptionResponseDto responseDto = optionsService.createOption(userId, surveyId, questionId, requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "선택지가 생성되었습니다.", responseDto));
    }

    @GetMapping("/{surveyId}/question/{questionId}/option")
    public ResponseEntity<ApiResponse<List<OptionResponseDto>>> getOptions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId
    ){
        Long userId = userDetails.getId();
        List<OptionResponseDto> responseDtoList = optionsService.getOptions(userId, surveyId, questionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "선택지 목록을 조회했습니다.", responseDtoList));
    }

    @PatchMapping("/{surveyId}/question/{questionId}/option/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<OptionResponseDto>> updateOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestBody OptionUpdateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        OptionResponseDto responseDto = optionsService.updateOption(userId, surveyId, questionId, optionId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "선택지가 수정되었습니다.", responseDto));
    }

    @DeleteMapping("/{surveyId}/question/{questionId}/option/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId
    )
    {
        Long userId = userDetails.getId();
        optionsService.deleteOption(userId, surveyId, questionId, optionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(true, "선택지가 삭제되었습니다.", null));
    }
}
