package com.example.surveyapp.domain.ai.chat.presentation;

import com.example.surveyapp.domain.ai.chat.presentation.dto.ChatRequestDto;
import com.example.surveyapp.domain.ai.chat.presentation.dto.ChatResponseDto;
import com.example.surveyapp.domain.ai.chat.presentation.dto.IndexRequestDto;
import com.example.surveyapp.domain.ai.chat.rag.DocumentIndexer;
import com.example.surveyapp.domain.ai.chat.application.ChatService;
import com.example.surveyapp.global.response.ApiResponse;
import com.example.surveyapp.global.security.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final DocumentIndexer indexer;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<ChatResponseDto>> ask(@RequestBody ChatRequestDto requestDto) {
        String answer = chatService.chat(requestDto.getQuestion());

        ChatResponseDto responseDto = ChatResponseDto.of(answer);

        return ResponseEntity.ok(ApiResponse.success("질의 성공", responseDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/index")
    public ResponseEntity<ApiResponse<ChatResponseDto>> indexText(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody IndexRequestDto requestDto) {

        indexer.indexText(userDetails.getId(), requestDto.getDocument());

        return ResponseEntity.ok(ApiResponse.success("문서가 인덱싱되었습니다.", null));
    }
}
