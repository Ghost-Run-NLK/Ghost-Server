# Ghost-Server

카트라이더 고스트 기능을 모티브로 한 러닝 앱 백엔드. 자세한 기능/API 스펙은 [`requirements.md`](./requirements.md), 개발 가이드는 [`CLAUDE.md`](./CLAUDE.md) 참조.

---

## 데모 모드 셋업

데모 단계에서는 로그인을 비활성화하고 클라가 매 호출에 `userId`(예: `user_1`)를 쿼리 파라미터로 전달합니다. 따라서 데모 시작 전 **유저 row 시드**가 필요합니다.

### 1) 유저 INSERT (예: 2명)

```sql
INSERT INTO users (social_provider, social_id, nickname, avatar_url, created_at, updated_at)
VALUES
  ('KAKAO', 'demo-1', '데모유저1', '/avatars/bear.png',    NOW(), NOW()),
  ('KAKAO', 'demo-2', '데모유저2', '/avatars/chicken.png', NOW(), NOW());
```

- 삽입된 row의 PK가 1, 2면 호출 시 `userId=user_1`, `userId=user_2`로 사용
- `social_provider`/`social_id`는 unique constraint(`uk_users_social`) 만족용 더미값

### 2) 프로필 이미지

`main-server/src/main/resources/static/avatars/` 안의 파일은 Spring Boot가 자동으로 정적 리소스로 서빙합니다.

- 접근 URL: `http://<host>:8080/avatars/bear.png` (또는 `chicken.png`)
- 이미지 교체/추가는 같은 폴더에 파일을 넣고 서버를 재시작
- 운영 단계에선 S3 같은 외부 스토리지로 이관 권장 (jar에 묶이지 않게)

### 3) 호출 예 (Swagger)

```
POST   /api/v1/runs?userId=user_1
POST   /api/v1/runs/run_1/locations?userId=user_1
PATCH  /api/v1/runs/run_1/stop?userId=user_1
GET    /api/v1/courses/course_1/leaderboard?userId=user_1
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html` (로컬 프로파일에선 `/swagger-ui.html`도 허용)
