package com.ghost.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 envelope (현재는 에러 응답에만 사용)")
public record ApiResponse<T>(
        @Schema(description = "HTTP 상태 코드", example = "404") int code,
        @Schema(description = "응답 메시지", example = "코스를 찾을 수 없습니다") String message,
        @Schema(description = "응답 데이터 (에러 시 null)", nullable = true) T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
