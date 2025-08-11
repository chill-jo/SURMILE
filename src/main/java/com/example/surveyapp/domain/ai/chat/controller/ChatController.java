package com.example.surveyapp.domain.ai.chat.controller;

import com.example.surveyapp.domain.ai.chat.controller.dto.ChatRequestDto;
import com.example.surveyapp.domain.ai.chat.controller.dto.ChatResponseDto;
import com.example.surveyapp.domain.ai.chat.service.ChatService;
import com.example.surveyapp.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<ChatResponseDto>> ask(@RequestBody ChatRequestDto requestDto) {
        String answer = chatService.chat(requestDto.getQuestion());
        ChatResponseDto responseDto = ChatResponseDto.of(answer);
        return ResponseEntity.ok(ApiResponse.success("질의 성공", responseDto));
    }
}
