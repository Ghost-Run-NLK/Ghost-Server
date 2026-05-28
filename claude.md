# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

카트라이더 고스트 기능을 모티브로 한 러닝 앱 백엔드. 과거 자신 또는 다른 유저의 기록을 고스트로 선택해 함께 달리는 경험을 제공.

기능 명세·전체 API 스펙·도메인 모델은 `requirements.md`가 진실 소스. 모호하면 거기서 출발한다.

---

## 빌드 / 실행 / 테스트

Gradle 멀티 모듈 (root + `main-server`). 모든 코드는 `main-server/` 안.

```bash
# 컴파일 (커밋 전 항상 통과 확인)
./gradlew :main-server:compileJava

# 로컬 서버 기동 (MySQL 필요, application-local.yaml 사용)
./gradlew :main-server:bootRun --args='--spring.profiles.active=local'

# 전체 테스트 (H2 인메모리, application-test.yaml)
./gradlew :main-server:test

# 단일 테스트
./gradlew :main-server:test --tests "com.ghost.server.common.config.SwaggerEndpointTest"
```

**환경변수 (선택)**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_SECONDS`. 미지정 시 `application-local.yaml` 디폴트 사용.

**Swagger UI**: 기동 후 `http://localhost:8080/swagger-ui/index.html` — endpoint 명세 + 동작 확인 모두 여기서.

---

## 기술 스택 (실측)

- **Java 25** (Gradle toolchain 강제)
- **Spring Boot 4.0.6** — webmvc / data-jpa / security / validation
- **springdoc-openapi 3.0.3** (Swagger UI)
- **jjwt 0.12.6** (HS256)
- **MySQL** (prod/local) / **H2** (test, MySQL 호환 모드)
- **Lombok**

Redis는 `claude.md` 구버전에 등장하지만 **현재 의존성 없음**. 도입 시 build.gradle.kts 부터 추가.

---

## 패키지 구조

Base package: **`com.ghost.server`** (구버전 claude.md의 `com.ghostrun`은 outdated).

도메인 단위로 먼저 나누고 각 도메인 안에서 layered.

```
com.ghost.server
├── MainServerApplication
├── common/
│   ├── config/        (SecurityConfig, SwaggerConfig, JpaAuditingConfig)
│   ├── entity/        (BaseEntity — id+createdAt+updatedAt 자동 부여)
│   ├── exception/     (ErrorCode, BusinessException, GlobalExceptionHandler)
│   ├── response/      (ApiResponse — 에러 envelope 전용)
│   ├── security/      (JwtTokenProvider, JwtAuthenticationFilter, JwtProperties)
│   └── util/          (GeoUtils=Haversine, PublicIdCodec)
└── domain/
    ├── user/          (User entity + lookup-or-create)
    ├── auth/          (소셜 로그인 stub + JWT 발급)
    ├── course/        (코스 read + admin CRUD)
    └── run/           (세션, 위치 배치, 종료, leaderboard, ghost — ghost는 별도 도메인 X)
```

각 도메인 내부 레이어: `controller / service / repository / entity / dto`.

---

## 아키텍처 원칙

- **계층 의존**: Controller → Service → Repository (역방향 금지)
- **DTO 변환은 service 레이어** 에서. 컨트롤러는 요청/응답 wiring만.
- **도메인 로직은 도메인 객체 안에** (예: `RunSession.complete()`, `Course.update()`)
- **도메인 간 호출은 service → service 만** 허용. 다른 도메인 repository 직접 접근 금지.
  - 예외: `RunSessionService.existsForCourse(courseId)` 처럼 thin facade를 노출해서 호출 측이 service만 의존하게 함
- 양방향 의존 자체는 허용 (CourseAdminService ↔ RunSessionService), constructor injection cycle만 피하면 됨

---

## 핵심 컨벤션

### API
- RESTful, `/api/v1` prefix, 소문자 + 하이픈
- **응답 포맷**: requirements.md 예시 그대로 — raw 객체 (envelope 없음)
- **에러만** `ApiResponse.error(code, message)` 형식: `{code, message, data: null}`

### 외부 노출 ID (중요)
모든 DB PK(Long)는 prefix가 붙은 string으로 직렬화: `course_1`, `run_2`, `user_3`. 변환은 **반드시** `common/util/PublicIdCodec` 사용.

```java
PublicIdCodec.encode("run_", id)       // → "run_42"
PublicIdCodec.decode("run_", input)    // → Optional<Long>
```

- decode 실패도 도메인의 `*_NOT_FOUND` 로 통일해서 던짐 (내부 구조/존재 노출 방지)
- 새 컨트롤러에서 prefix 상수를 private static으로 하나 들고 사용

### 예외 처리
- 도메인 에러는 `BusinessException(ErrorCode.XXX)` 던지면 `GlobalExceptionHandler` 가 HTTP status + ApiResponse 로 변환
- 새 ErrorCode는 `common/exception/ErrorCode.java` 에 추가
- 다른 유저 소유 리소스 접근 시도는 `*_NOT_FOUND` 로 통일 (FORBIDDEN 아님 — 소유자 노출 방지)

### 인증 / 권한
- JWT, `Authorization: Bearer <token>`. principal로 raw `Long(userId)` 사용
- 컨트롤러에서 `@AuthenticationPrincipal Long currentUserId` 로 추출 (항상 non-null 가정)
- **게스트 진입 없음** — 모든 endpoint 인증 필수. `/swagger-ui/**`, `/v3/api-docs/**`, `/api/v1/auth/**` 만 permitAll
- SecurityConfig: STATELESS + `anonymous().disable()`
- ADMIN role 가드는 아직 없음 — `/admin/**` 도 현재는 인증만 요구 (TODO: 후속 PR)

### Swagger 명세
컨트롤러 추가 시 이 패턴 그대로 적용:
- 클래스: `@Tag(name = "<도메인>", description = "...")`
- 메서드: `@Operation(summary, description)` + `@ApiResponses({200, 도메인 에러 4xx/5xx})`
- 에러 응답 schema는 `ApiResponse.class`, `@ExampleObject` value 에 ErrorCode 메시지 인용
- path/query: `@Parameter(description, example)`
- DTO record 컴포넌트마다 `@Schema(description, example)` — example은 requirements.md 그대로

`CourseController` / `RunController` 가 살아있는 reference.

### 네이밍
Class PascalCase / method·variable camelCase / 상수 UPPER_SNAKE_CASE / 테이블 snake_case.

---

## 핵심 비즈니스 로직 (요약)

자세한 흐름·필드는 requirements.md.

- **러닝 라이프사이클**: `POST /runs` → `POST /runs/{id}/locations` (반복) → `PATCH /runs/{id}/stop`
- **유저당 ACTIVE 1개** 정책 — start 시 기존 ACTIVE 를 ABANDONED 로 자동 폐기 (`findByUserIdAndStatus` + `RunSession.abandon`)
- **위치 배치 dedup**: 요청 내부 t 중복 제거 + DB 기존 t 조회로 추가 dedup. `uk_track_run_t` unique 제약이 race condition 백업
- **고스트 데이터**: 시작 시 ghost trackPoints 전체를 한 번에 응답 (서버 실시간 통신 없음)
- **리더보드**: COMPLETED top 10, `totalTime ASC, endedAt ASC` (동률은 먼저 완주한 사람). `idx_run_leaderboard` 활용
- **stop 응답의 rank/isNewRecord**: count(totalTime < me) + 1 / 본인 이전 최단 기록 대비
- **RUN 활성화 반경 1m**: `GeoUtils.distanceMeters` (Haversine)
- **푸시 알림**: 인터페이스/이벤트 아무것도 없음. 알림 도메인 도입 시 한 번에

### 주요 상태값
`RunSession.status`: `ACTIVE | COMPLETED | ABANDONED`

### 소셜 로그인
현재 `KakaoAuthClient` / `AppleAuthClient` 는 **stub** — 받은 token을 그대로 socialId로 사용. 실 OAuth 연동은 후속 PR.

---

## 브랜치 / 커밋 규칙

- 항상 **develop 에서 작업 브랜치 분기**
- 작업 단위로 브랜치 — 형식: `type/domain-feature` (예: `feat/run-locations-stop`, `chore/swagger-docs`)
- 커밋도 유의미한 단위로 쪼개기 — 한 PR에 5개 안팎이 흔함
- 커밋 메시지 한국어: `type(scope?): 내용`
- type: `feat`, `fix`, `refactor`, `test`, `chore`, `docs`

PR 본문은 작성해서 사용자에게 전달, 실제 생성/머지는 사용자가 수행.

---

## 참고 문서

- **`requirements.md`** — 기능 요구사항, API 8종 명세, 도메인 모델 (진실 소스)
