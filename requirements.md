# 고스트 러닝 앱 요구사항 명세서

## 프로젝트 개요

카트라이더 고스트 기능을 모티브로 한 러닝 앱.  
과거 자신의 기록 또는 다른 유저의 기록을 고스트로 선택해 함께 달리는 경험을 제공한다.

---

## 기능 요구사항

### 1. 소셜 로그인
- 소셜 계정(Kakao, Apple)을 통한 로그인

### 2. 메인 화면
- 코스 리스트 표시 (관리자가 등록한 코스)
- 코스별 이름, 주소, 거리 표시

### 3. 코스 선택
- 코스 지도 뷰 표시
- 상위 10등 기록 기반 고스트 선택 (스크롤로 10등까지 확인)
    - 고스트를 이기면 해당 유저에게 푸시 알림 발송
- RUN 버튼 활성화 조건
    - 지정 출발지로부터 반경 1m 이내에 있어야 활성화

### 4. 러닝 중 화면
- 지도 뷰
    - 내 현재 위치 표시
    - 내가 지나온 길 (폴리라인)
    - 남은 경로
    - 고스트 위치 (페이스에 따른 이동)
- 실시간 러닝 정보 표시
    - 뛴 시간
    - 뛴 거리
    - 평균 페이스
- 지정 도착지 도달 시 자동 종료

### 5. 관리자 페이지
- 코스 경로 설정 및 관리

---

## MVP 제외 항목
- 지역 필터링
- 프로필 / 마이페이지

---

## 기술 결정사항

### 고스트 데이터 처리
- 러닝 시작 시 ghost trackPoints 전체를 한 번에 응답으로 전달
- 서버 실시간 통신 불필요

### 클라이언트 위치 수신
- 방식: 배치 전송 (REST POST)
- 배치 수신 시 서버에서 `elapsedSec` 기준 정렬 후 저장 (순서 역전 대응)
- 중복 포인트 `elapsedSec` 기준으로 필터링 후 저장

---

## API 명세

### 1. 코스 리스트 조회

```
GET /api/v1/courses
```

**Response**
```json
{
  "courses": [
    {
      "courseId": "course_001",
      "name": "성성호수 공원",
      "address": "천안시 서북구 천안대로 1223-24",
      "distance": 3500,
      "routePoints": [
        { "lat": 36.8097, "lng": 127.0079 },
        { "lat": 36.8101, "lng": 127.0085 },
        { "lat": 36.8120, "lng": 127.0102 },
        { "lat": 36.8155, "lng": 127.0138 },
        { "lat": 36.8190, "lng": 127.0170 },
        { "lat": 36.8210, "lng": 127.0190 }
      ]
    },
    {
      "courseId": "course_002",
      "name": "단대호수 공원",
      "address": "천안시 동남구 단대로 100",
      "distance": 5000,
      "routePoints": [
        { "lat": 36.8050, "lng": 127.1500 },
        { "lat": 36.8062, "lng": 127.1521 },
        { "lat": 36.8090, "lng": 127.1565 },
        { "lat": 36.8128, "lng": 127.1610 }
      ]
    }
  ]
}
```

- 화면이 각 코스 카드에 지도 라인을 그리므로 `routePoints` 좌표 배열을 함께 내려준다 (sequence 오름차순)
- 출발/도착 좌표는 상세 조회(`GET /api/v1/courses/{courseId}`)에서만 제공

---

### 2. 코스 상세 조회

```
GET /api/v1/courses/{courseId}
```

**Response**
```json
{
  "courseId": "course_001",
  "name": "성성호수 공원",
  "address": "천안시 서북구 천안대로 1223-24",
  "distance": 3500,
  "startLat": 36.8097,
  "startLng": 127.0079,
  "endLat":   36.8210,
  "endLng":   127.0190,
  "routePoints": [
    { "lat": 36.8097, "lng": 127.0079 },
    { "lat": 36.8098, "lng": 127.0080 },
    ...
  ]
}
```

---

### 3. 리더보드 (고스트 선택 화면)

```
GET /api/v1/courses/{courseId}/leaderboard
```

**Response**
```json
{
  "course": {
    "courseId": "course_001",
    "name": "성성호수 공원",
    "distance": 3500
  },
  "entries": [
    {
      "rank": 1,
      "runId": "run_abc123",
      "userId": "user_001",
      "nickname": "달리기 장인",
      "avatarUrl": "https://...",
      "totalTime": 762,
      "avgPace": "06:32",
      "completedAt": "2026-05-01T06:12:00Z",
      "isMe": false
    }
  ]
}
```

- 상위 10등까지 반환
- `isMe`: 내 기록 여부

---

### 4. 러닝 세션 시작

```
POST /api/v1/runs
```

**Request**
```json
{
  "courseId": "course_001",
  "ghostRunId": "run_abc123"
}
```

**Response**
```json
{
  "runId": "run_xyz789",
  "status": "ACTIVE",
  "ghost": {
    "runId": "run_abc123",
    "nickname": "달리기 장인",
    "avatarUrl": "https://...",
    "totalTime": 762,
    "avgPace": "06:32",
    "trackPoints": [
      { "elapsedSec": 0,   "lat": 36.8097, "lng": 127.0079 },
      { "elapsedSec": 2,   "lat": 36.8098, "lng": 127.0080 },
      ...
      { "elapsedSec": 762, "lat": 36.8210, "lng": 127.0190 }
    ]
  }
}
```

- `ghost.trackPoints`: 클라이언트 고스트 위치 계산용으로 한 번에 전달

---

### 5. 위치 배치 수신

```
POST /api/v1/runs/{runId}/locations
```

**Request**
```json
{
  "points": [
    { "elapsedSec": 0,  "lat": 36.8097, "lng": 127.0079 },
    { "elapsedSec": 2,  "lat": 36.8098, "lng": 127.0080 },
    ...
    { "elapsedSec": 30, "lat": 36.8110, "lng": 127.0092 }
  ]
}
```

**Response**
```json
{
  "receivedCount": 15,
  "lastReceivedT": 30
}
```

---

### 6. 러닝 종료

```
PATCH /api/v1/runs/{runId}/stop
```

**Request** — body 없음

**Response**
```json
{
  "runId": "run_xyz789",
  "status": "COMPLETED",
  "isNewRecord": true,
  "rank": 1
}
```

- 다음 값은 서버에서 계산해서 저장:
  - `endedAt` = 서버 `now()`
  - `totalTime` = `endedAt - startedAt` (초)
  - `distance` = 수신된 trackPoints 누적 Haversine (m)
  - `avgPace` = `totalTime / (distance / 1000)` → `MM:SS` 포맷 (distance 0이면 `"00:00"`)
- 종료 즉시 리더보드 반영 후 순위 응답

---

## 전체 플로우

```
[코스 리스트]
GET /courses
        ↓
[코스 상세 + 고스트 선택]
GET /courses/{courseId}
GET /courses/{courseId}/leaderboard
        ↓
[러닝 시작]
POST /runs  →  ghost trackPoints 한 번에 수신
        ↓
[러닝 중]
POST /runs/{id}/locations  (배치)
        ↓
[러닝 종료]
PATCH /runs/{id}/stop  →  요약 데이터 + 순위 결과
```

---

## 도메인 모델

```
User
  - userId
  - nickname
  - avatarUrl
  - createdAt

Course
  - courseId
  - name
  - address
  - distance
  - startLat / startLng
  - endLat / endLng
  - routePoints             // 경로 좌표 (관리자 설정)

RunSession
  - runId
  - userId
  - courseId
  - ghostRunId              // 선택한 고스트 기록 ID
  - status                  // ACTIVE / COMPLETED
  - startedAt
  - endedAt
  - totalTime
  - distance
  - avgPace

TrackPoint
  - id
  - runId
  - elapsedSec              // 러닝 시작 이후 경과 시간 (초)
  - lat
  - lng
```