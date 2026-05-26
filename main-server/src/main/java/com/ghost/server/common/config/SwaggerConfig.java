package com.ghost.server.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String API_DESCRIPTION = """
            고스트 러닝 앱 백엔드 API 명세

            ---

            ### 정적 리소스 (Swagger 자동 스캔 대상 외)

            프로필 이미지는 Spring Boot 정적 리소스로 서빙됩니다. \
            컨트롤러가 아니라 Swagger 목록에는 안 잡히지만, 아래 URL로 직접 접근 가능:

            - `GET /avatars/bear.png`
            - `GET /avatars/chicken.png`

            예: `http://localhost:8080/avatars/bear.svg`. \
            DB `users.avatar_url`에 `/avatars/bear.png` 형태로 저장해두면 리더보드 응답의 \
            `entries[].avatarUrl`이 그 경로로 떨어지고, 프론트가 `<img src>`에 사용.

            ### 데모 모드 인증

            로그인은 데모 단계 비활성. 인증 필요 endpoint는 `?userId=user_1` 형태로 \
            쿼리 파라미터에 직접 유저 ID를 전달.
            """;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ghost API")
                        .version("1.0.0")
                        .description(API_DESCRIPTION))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")
                ));
    }
}
