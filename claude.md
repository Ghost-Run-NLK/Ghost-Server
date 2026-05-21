# CLAUDE.md

## 프로젝트 개요

카트라이더 고스트 기능을 모티브로 한 러닝 앱 백엔드.  
자세한 기능 요구사항 및 API 명세는 `requirements.md` 참고.

---

## 기술 스택

- **Language**: Java 25
- **Framework**: Spring Boot 3.x
- **ORM**: Spring Data JPA
- **Database**: MySQL
- **Cache**: Redis
- **Build**: Gradle

---

## 패키지 구조

도메인 단위로 먼저 나누고, 각 도메인 안에서 레이어드 아키텍처 적용.

```
com.ghostrun
  ├── domain
  │   ├── user
  │   │   ├── controller
  │   │   ├── service
  │   │   ├── repository
  │   │   ├── entity
  │   │   └── dto
  │   ├── course
  │   │   ├── controller
  │   │   ├── service
  │   │   ├── repository
  │   │   ├── entity
  │   │   └── dto
  │   ├── run
  │   │   ├── controller
  │   │   ├── service
  │   │   ├── repository
  │   │   ├── entity
  │   │   └── dto
  │   └── ghost
  │       ├── controller
  │       ├── service
  │       ├── repository
  │       ├── entity
  │       └── dto
  └── global
      ├── exception
      ├── config
      └── util
```

---

## 아키텍처 원칙

- 계층 구조: Controller → Service → Repository
- DTO 변환은 Service 레이어에서 처리
- 도메인 로직은 도메인 객체 안에
- Controller는 요청/응답 처리만

---

## 컨벤션

### API
- RESTful 설계
- URL: 소문자 + 하이픈 (`/api/v1/runs/{runId}/locations`)
- 버전 prefix: `/api/v1`
- 응답 형식 통일

```json
// 성공
{
  "data": { ... }
}

// 실패
{
  "code": "RUN_NOT_FOUND",
  "message": "러닝 세션을 찾을 수 없습니다."
}
```

### 예외 처리
- `GlobalExceptionHandler`로 통합 처리
- 커스텀 예외 클래스 사용 (`RunNotFoundException` 등)

### 네이밍
- Class: PascalCase
- Method/Variable: camelCase
- Constant: UPPER_SNAKE_CASE
- Table: snake_case

---

## 핵심 비즈니스 로직

### 위치 배치 수신
- `t` 기준으로 오름차순 정렬 후 저장 (순서 역전 대응)
- 중복 포인트 `t` 기준으로 필터링 후 저장

### 고스트 데이터
- 러닝 시작 시 `trackPoints` 전체를 응답에 포함해서 한 번에 전달
- 서버 실시간 통신 없음

### 러닝 종료 시
- 요약 데이터 저장
- 리더보드 즉시 반영
- 기존 1등 기록 보유자에게 푸시 알림 발송 (고스트를 이긴 경우)

### RUN 버튼 활성화 조건 검증
- Haversine 공식으로 출발지와 현재 위치 간 거리 계산
- 반경 1m 이내 여부 응답

---

## 주요 상태값

### RunSession.status
```
ACTIVE     러닝 진행 중
COMPLETED  러닝 종료
```

---

## 참고 문서

- `requirements.md`: 기능 요구사항 및 API 명세 전체


## 브랜치 규칙

- 작업 단위로 브랜치 생성 후 작업
- develop 브랜치에서 작업 브랜치 생성
- 브랜치 형식: `type/domain-feature`
```
feat/course-api
feat/run-api
feat/leaderboard-api
feat/auth
fix/track-point-duplicate
refactor/run-service
```

- 브랜치 type 종류: `feat`, `fix`, `refactor`, `chore`
---

## 커밋 규칙

- 작업 완료 시 유의미한 단위로 쪼개서 커밋
- 여러 작업을 한 번에 모아서 커밋하지 않음
- 커밋 메시지 형식: `type: 내용`
```
feat: 러닝 세션 시작 API 구현
feat: 위치 배치 수신 API 구현
feat: 리더보드 조회 API 구현
refactor: RunService 중복 로직 제거
fix: TrackPoint 중복 저장 버그 수정
```

- type 종류: `feat`, `fix`, `refactor`, `test`, `chore`