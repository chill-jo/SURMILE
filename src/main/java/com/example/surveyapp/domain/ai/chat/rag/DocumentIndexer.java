package com.example.surveyapp.domain.ai.chat.rag;

import com.example.surveyapp.domain.ai.exception.AiErrorCode;
import com.example.surveyapp.domain.user.domain.model.UserRoleEnum;
import com.example.surveyapp.global.reader.UserReader;
import com.example.surveyapp.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentIndexer {
    private final VectorStore vectorStore;
    private final UserReader userReader;

    public void indexText(Long userId, String content) {
        validateIndexerAccess(userId);

        Document doc = new Document(content);
        vectorStore.add(List.of(doc));
    }

    public void validateIndexerAccess(Long userId){
        if (userId == null || !userReader.validateUserRole(userId, UserRoleEnum.ADMIN)) {
            throw new CustomException(AiErrorCode.NOT_MATCH_ADMIN);
        }
    }
}
