package com.ghost.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 소셜 로그인
    SOCIAL_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "SOCIAL_AUTH_FAILED", "소셜 인증에 실패했습니다"),
    SOCIAL_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "SOCIAL_TOKEN_INVALID", "소셜 인증 토큰이 유효하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
