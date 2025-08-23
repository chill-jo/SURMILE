package com.example.surveyapp.domain.ai.init;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDocumentLoader {
	private final InitialDocumentSaver initialDocumentSaver;

	// 초기 요구사항 문서 등록
	@PostConstruct
	public void loadInitialDocuments() {
		initialDocumentSaver.loadInitialDocuments();
	}
}
