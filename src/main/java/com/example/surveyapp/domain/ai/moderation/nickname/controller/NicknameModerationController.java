package com.example.surveyapp.domain.ai.moderation.nickname.controller;

import com.example.surveyapp.domain.ai.moderation.nickname.controller.dto.NicknameModerationRequestDto;
import com.example.surveyapp.domain.ai.moderation.nickname.controller.dto.NicknameModerationResponseDto;
import com.example.surveyapp.domain.ai.moderation.nickname.service.NicknameModerationService;
import com.example.surveyapp.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/moderation/nickname")
public class NicknameModerationController {
    private final NicknameModerationService nicknameModerationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NicknameModerationResponseDto>> checkNickname(
            @RequestBody @Valid NicknameModerationRequestDto request
    ) {
        NicknameModerationResponseDto response = nicknameModerationService.moderate(request);

        return ResponseEntity.ok(ApiResponse.success("판단에 성공했습니다.", response));
    }
}
