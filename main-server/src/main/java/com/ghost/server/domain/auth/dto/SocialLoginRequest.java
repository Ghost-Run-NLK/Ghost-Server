package com.ghost.server.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "소셜 로그인 요청")
public record SocialLoginRequest(
        @Schema(description = "소셜 provider가 발급한 access token (현재 stub: 임의 문자열)",
                example = "kakao-test-token-001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String socialAccessToken
) {
}
