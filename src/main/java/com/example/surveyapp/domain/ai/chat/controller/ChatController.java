package com.example.surveyapp.domain.ai.chat.controller;

import com.example.surveyapp.domain.ai.chat.controller.dto.ChatRequestDto;
import com.example.surveyapp.domain.ai.chat.controller.dto.ChatResponseDto;
import com.example.surveyapp.domain.ai.chat.controller.dto.IndexRequestDto;
import com.example.surveyapp.domain.ai.chat.rag.DocumentIndexer;
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
    private final DocumentIndexer indexer;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<ChatResponseDto>> ask(@RequestBody ChatRequestDto requestDto) {
        String answer = chatService.chat(requestDto.getQuestion());
        ChatResponseDto responseDto = ChatResponseDto.of(answer);
        return ResponseEntity.ok(ApiResponse.success("질의 성공", responseDto));
    }

    @PostMapping(value = "/index")
    public ResponseEntity<ApiResponse<ChatResponseDto>> indexText(@RequestBody IndexRequestDto requestDto) {
        String content = requestDto.getDocument();
        indexer.indexText(content);
        return ResponseEntity.ok(ApiResponse.success("문서가 인덱싱되었습니다.", null));
    }
}
