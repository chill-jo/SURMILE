package com.example.surveyapp.domain.ai.chat.service;

import com.example.surveyapp.domain.ai.chat.prompt.ChatPromptTemplate;
import com.example.surveyapp.domain.ai.chat.rag.DocumentSearcher;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;
    private final DocumentSearcher searcher;
    private final ChatPromptTemplate promptTemplate;

    @Transactional
    public String chat(String question) {
        List<Document> docs = searcher.search(question, 4, 0.7);

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        Prompt prompt = promptTemplate.build(context, question);

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
