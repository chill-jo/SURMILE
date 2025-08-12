package com.example.surveyapp.domain.survey.presentation;

import com.example.surveyapp.domain.survey.presentation.dto.request.OptionCreateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.request.OptionUpdateRequestDto;
import com.example.surveyapp.domain.survey.presentation.dto.response.OptionResponseDto;
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
    public ResponseEntity<OptionResponseDto> createOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @RequestBody OptionCreateRequestDto requestDto
    ){
        Long userId = userDetails.getId();
        OptionResponseDto responseDto = optionsService.createOption(userId, surveyId, questionId, requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/{surveyId}/question/{questionId}/option")
    public ResponseEntity<List<OptionResponseDto>> getOptions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId
    ){
        Long userId = userDetails.getId();
        List<OptionResponseDto> responseDtoList = optionsService.getOptions(userId, surveyId, questionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDtoList);
    }

    @PatchMapping("/{surveyId}/question/{questionId}/option/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<OptionResponseDto> updateOption(
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
                .body(responseDto);
    }

    @DeleteMapping("/{surveyId}/question/{questionId}/option/{optionId}")
    @PreAuthorize("hasAnyRole('ADMIN','SURVEYOR')")
    public ResponseEntity<Void> deleteOption(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId
    )
    {
        Long userId = userDetails.getId();
        optionsService.deleteOption(userId, surveyId, questionId, optionId);

        return ResponseEntity
                .ok()
                .body(null);
    }
}
