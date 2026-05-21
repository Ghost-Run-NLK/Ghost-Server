package com.ghost.server.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 응답")
public record LoginResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...") String accessToken,
        @Schema(description = "로그인된 유저 정보") AuthUserDto user
) {
}
