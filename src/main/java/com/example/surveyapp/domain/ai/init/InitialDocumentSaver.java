package com.example.surveyapp.domain.ai.init;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDocumentSaver {
	private final VectorStore vectorStore;

	@Async
	public void loadInitialDocuments() {
		List<Document> documents = List.of(
			new Document("출제자는 회원 가입 시 출제자로 선택해야 설문조사 등록 등 출제자의 기능을 이용할 수 있습니다."),
			new Document("출제자는 이메일과 비밀번호로 로그인할 수 있으며, 참여자 기능(상점, 설문 참여)을 이용할 수 없습니다."),
			new Document("출제자는 설문을 생성하기 위해 최소 5,000원을 충전해야 합니다."),
			new Document("설문 생성 시 지급 포인트는 참여자 1인당 최소 10P 이상, 10P 단위로 설정해야 합니다."),
			new Document("설문은 최소 10명의 참여 인원을 설정해야 하며, 포인트 예산은 선차감됩니다."),
			new Document("마감일은 생성일 이후의 날짜만 가능하며, 최소 1일 이상이어야 합니다."),
			new Document("진행 중인 설문이 아니라면 지급 포인트 및 마감일자 수정이 가능합니다."),
			new Document("출제자는 생성한 설문 상태를 시작 전 → 진행 중 → 마감 순으로 변경할 수 있습니다."),
			new Document("진행 중에서 시작 전으로는 되돌릴 수 없습니다."),
			new Document("설문이 마감되면 남은 예산은 환불됩니다."),
			new Document("참여자는 회원 가입 시 참여자로 선택해야 하며, 설문 등록은 불가합니다."),
			new Document("참여자는 설문에 참여하고 완료하면 포인트를 즉시 지급받습니다."),
			new Document("동시에 응시한 경우 먼저 제출한 참여자가 포인트를 받습니다."),
			new Document("참여자는 동일 설문에 1회만 참여할 수 있습니다."),
			new Document("참여자는 마이페이지에서 완료한 설문 및 포인트, 교환한 상품을 확인할 수 있습니다."),
			new Document("참여자는 상점에서 카테고리별 상품을 조회하고 포인트로 상품 1개를 교환할 수 있습니다."),
			new Document("관리자는 상품을 등록, 수정, 상태 변경, 삭제(soft delete)할 수 있습니다."),
			new Document("관리자는 블랙리스트를 등록하고, 운영 통계를 조회할 수 있습니다."),
			new Document("운영 통계에는 상품 교환 순위, 설문 참여율, 출제자 포인트 충전량, 참여자 보유 포인트가 포함됩니다.")
		);
		vectorStore.add(documents);
	}
}
