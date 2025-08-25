# 🖥️ 프로젝트 소개
# 😀써마일(surmile)💵

## **survey + smile = surmile**

### “설문에 웃음을 더하는 설문조사 기반 앱테크 리워드 플랫폼”

>짧은 시간 투자로 포인트를 적립하고 보상을 받는 **앱테크 문화**가 빠르게 확산되고 있습니다.
동시에 개인·기업은 **온라인에서 손쉽게 설문을 배포하고 다수의 의견을 모으는 수요**가 커지고 있죠.

> 이러한 니즈를 바탕으로
**“설문 참여 ↔ 포인트 적립 ↔ 상품 교환”**을 하나로 잇는 통합 리워드 설문 플랫폼, **써마일**을 기획했습니다.

> **써마일**에서는 사용자가 다양한 설문에 참여하면 포인트를 획득하고, 이를 원하는 상품으로 교환할 수 있습니다.
출제자는 개인·기업 누구나 필요 설문을 손쉽게 배포해 더 많은 응답을 빠르게 수집할 수 있어요.

## 📢 _**설문은 간단하게, 보상은 확실하게!**_

---
# 📌 주요 기능
- **유저(User)**: 회원가입, 로그인, JWT 인증, 프로필 관리

- **설문(Survey)**: 설문 생성, 수정, 참여, 응답 저장, 통계 제공

- **주문(Order)**: 설문 참여 포인트 결제, 주문 관리

- **상품(Product)**: 포인트 교환 상품 관리

- **포인트(Point)**: 포인트 지급/차감, 포인트 내역 저장

- **결제(Payment)**: 설문 출제자 포인트 결제

- **AI**: 설문 가이드 챗봇, 내용 검수(Moderation)

---

# 📈 기술적 고도화
- OAuth2 **소셜 로그인** 적용
- AI 기반 **Moderation 및 Chatbot** 적용으로 UX 향상
- **CI/CD** 환경 구축 및 자동 배포
- **DDD 기반 구조**로 의존성 최소화 및 유지보수성 향상
- 이벤트 기반 **Saga, Outbox 패턴** 적용으로 비동기 처리 안정성 확보
- 데이터 정합성을 위한 **락(Lock**)과 **캐시(Cache)** 최적화

---

# 📦개발 환경 소개
| 분류         | 상세                                               |
| ------------ |--------------------------------------------------|
| IDE          | IntelliJ IDEA                                    |
| Language     | Java 17                                          |
| Framework    | Spring Boot 3.5.3                                |
| Repository   | Maven Central, Spring Milestone, Spring Snapshot |
| Build Tool   | Gradle                                           |
| DevOps       | AWS EC2, Docker, Docker Compose, GitHub Actions  |
| DB           | MySQL, H2 (Test), Redis                          |
| Security     | Spring Security, JWT (jjwt 0.12.5)               |
| Cache        | Redis, Redisson                                  |
| Testing      | JUnit 5, Spring Boot Test, Testcontainers        |
| Documentation| Spring REST Docs, Asciidoctor                    |
| AI | Spring AI, OpenAI API, Simple Vectorstore        |

---

# 📋프로젝트 실행 방법
## 프로젝트 환경변수 설정

프로젝트 실행을 위해 아래와 같은 `.env` 파일을 루트 경로에 생성합니다.
```
## DB 설정
DB_URL=jdbc:mysql://localhost:3306/survey_app
DB_HOST=localhost
DB_PORT=3306
DB_NAME=sparta
DB_USERNAME=root
DB_PASSWORD=password

REDIS_HOST=localhost
REDIS_PORT=6379

## 외부 API 키
SECRET_KEY=your_super_secret_key_here
OPENAI_API_KEY=your_api_key_here 
```
---
# 🛠️설계 산출물
### Architecture & Diagrams
[📚 ERD (Wiki)](https://github.com/chill-jo/surveyApp/wiki/ERD) <br>
[📚 Event Storming (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Event-Storming) <br>
[📚 Use Case Diagram (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Use-Case-Diagram) <br>
[📚 Bounded Context (Wiki)](https://github.com/chill-jo/surveyApp/wiki/BoundedContext)<br>
[📚 Aggregate (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Aggregate)<br>
[📚 Event Flow (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Event-Flow)<br>
[📚 Deployment Diagram (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Deployment-Diagram)<br>

### Conventions
[📚 Java Code Style (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Conventions-%E2%80%90-JavaCodeStyle)<br>
[📚 Directory Structure (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Conventions-%E2%80%90-Directory-Structure)<br>
[📚 Git Convention (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Conventions-%E2%80%90-Git-Convention)<br>
[📚 Common Api Response (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Conventions-%E2%80%90-Common-API-Response)<br>

### API 명세서
[📚 Api 명세서 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/API-%EB%AA%85%EC%84%B8%EC%84%9C)

---

# 🗃️️️개발 산출물

## 💡기술적 의사결정
- 기술적 의사결정#1 - [📚 CustomUserDetails (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90-CustomUserDetails)<br>
- 기술적 의사결정#2 - [📚 Spring AI(Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90--Spring-AI)<br>
- 기술적 의사결정#3 - [📚 Domain-Driven Design (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90-Domain%E2%80%90Driven-Design)<br>
- 기술적 의사결정#4 - [📚 Lock (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90--Lock)<br>
- 기술적 의사결정#5 - [📚 Cache (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90-Cache)<br>
- 기술적 의사결정#6 - [📚 Spring RestDocs (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90--Spring-RestDocs)<br>
- 기술적 의사결정#7 - [📚 OAUTH2 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90-OAUTH2)<br>
- 기술적 의사결정#8 - [📚 부하테스트 선택 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90--%EB%B6%80%ED%95%98%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%84%A0%ED%83%9D)<br>
- 기술적 의사결정#9 - [📚 CI/CD (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Decision-Log-%E2%80%90--CI-CD)<br>

## 🐞트러블 슈팅

- 트러블슈팅#1 - [📚 이벤트 기반 비동기 처리로 인한 데이터 정합성 문제 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Troubleshooting-%E2%80%90-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EA%B8%B0%EB%B0%98-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%B2%98%EB%A6%AC%EB%A1%9C-%EC%9D%B8%ED%95%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%95%ED%95%A9%EC%84%B1-%EB%AC%B8%EC%A0%9C)<br>
- 트러블슈팅#2 - [📚 DeadLock, Lost Update (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Troubleshooting-%E2%80%90-DeadLock,--Lost-Update-%EB%AC%B8%EC%A0%9C)<br>
- 트러블슈팅#3 - [📚 자바 LocalDateTime 타입 변환 에러 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Troubleshooting-%E2%80%90-%EC%9E%90%EB%B0%94-LocalDateTime-%ED%83%80%EC%9E%85-%EB%B3%80%ED%99%98-%EC%97%90%EB%9F%AC)<br>
- 트러블슈팅#4 - [📚 소셜 로그인 구현 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Troubleshooting-%E2%80%90-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84)<br>

## 📈성능 테스트

- 동시성 제어 테스트 - [📚 동시성 제어 테스트 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Performance-Testing-%E2%80%90-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4-%ED%85%8C%EC%8A%A4%ED%8A%B8)<br>
- 캐시 성능 테스트 - [📚 캐시 성능 테스트 (Wiki)](https://github.com/chill-jo/surveyApp/wiki/Performance-Testing-%E2%80%90-%EC%BA%90%EC%8B%9C-%EC%84%B1%EB%8A%A5-%ED%85%8C%EC%8A%A4%ED%8A%B8)<br>

---

# 🔧 앞으로의 시스템 개선 계획

- #  📬 메시지 큐 도입

    - 안정적인 이벤트 발행으로 데이터 정합성 강화

    - 대규모 트래픽에서도 이벤트를 정확하게 처리 가능

- # 💬 WebSocket 알림 기능

    - 실시간 알림 지원

    - 사용자 경험 개선 및 즉각적인 피드백 제공

- # 🤖 AI 기능 개선
    - ## 성능 최적화
        - ### Moderation

            - 응답 지연 최소화, 실시간 처리 향상

            - 부적절한 응답 AI 판정과 관리자 판정 비교 → 프롬프트 개선

            - 부적절한 응답 발생 시 메시지 큐를 통해 관리자에게 실시간 알림

        - ### ChatBot
            - 벡터스토어에 Redis / Elasticsearch DB 활용

- # 🌐 분산 시스템을 위한 HTTP Client 도입

    - MSA 구조 도입을 위한 외부 API 통신 최적화

    - Spring WebClient 활용, 비동기 처리 및 재시도 로직 적용



- # 🔒 Lock 개선

    - DB Lock: 포인트(Point) 도메인 안정성 확보

    - 대규모 트래픽 환경에서는 DB 부하 최소화를 위해 분산 락 도입 검토

- # 🤝 협업 시 잘한 것들
    - github tracking board 활용 - issue, pr을 통해 진행 상황 관리
    - 소통 - 매일 아침 데일리 스크럼으로 진행 계획 공유
    - 코드 리뷰 - Pull Request 리뷰로 코드 품질 확보
    - 기능 구현 후 테스트 코드 작성
    - 컨벤션 준수 - 코드 스타일, Git commit 규칙 통일



# 😅 협업 시 아쉽거나 부족했던 부분들

- 기술 습득 시간 - 일부 기능 시간 문제로 도입 실패
- 설계 미흡 - 일부 기능 구현 전 설계가 충분하지 않아 구현 및 테스트 과정에서 재작업 발생
- 작업 내용 공유에 어려움

# 👥 역할 분담 및 협업 방식

| 이름 | 역할                                                       |
|----|----------------------------------------------------------|
|김도한| Product, Order 도메인<br/>DDD 구조, 이벤트 SAGA, Outbox 패턴       |
|김민성| Survey, SurveyAnswer 도메인<br/>DDD 구조, 이벤트 SAGA, Outbox 패턴 |
|김민진| User 도메인<br/>AI Moderation, Chatbot                      |
|김형우| Admin 도메인<br/>CI/CD 파이프라인 구축, Cache 성능 개선, token refresh |
|안지현| 인증인가<br/>소셜로그인                                           |
|장아영| Point 도메인<br/>Lock 성능 개선                                 |
