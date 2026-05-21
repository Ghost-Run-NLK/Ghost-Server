package com.ghost.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 소셜 로그인
    SOCIAL_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "SOCIAL_AUTH_FAILED", "소셜 인증에 실패했습니다"),
    SOCIAL_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "SOCIAL_TOKEN_INVALID", "소셜 인증 토큰이 유효하지 않습니다"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "유저를 찾을 수 없습니다"),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "코스를 찾을 수 없습니다"),

    // Run
    RUN_NOT_FOUND(HttpStatus.NOT_FOUND, "RUN_NOT_FOUND", "러닝 세션을 찾을 수 없습니다"),
    RUN_ALREADY_ACTIVE(HttpStatus.CONFLICT, "RUN_ALREADY_ACTIVE", "이미 진행 중인 러닝 세션이 있습니다"),
    RUN_NOT_ACTIVE(HttpStatus.CONFLICT, "RUN_NOT_ACTIVE", "진행 중인 러닝 세션이 아닙니다"),
    GHOST_RUN_NOT_FOUND(HttpStatus.NOT_FOUND, "GHOST_RUN_NOT_FOUND", "고스트 기록을 찾을 수 없습니다"),
    OUT_OF_START_RADIUS(HttpStatus.BAD_REQUEST, "OUT_OF_START_RADIUS", "출발지 반경을 벗어났습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
