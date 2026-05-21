package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Schema(description = "러닝 세션 종료 요청 (요약 데이터 + 클라 계산 칼로리)")
public record RunStopRequest(
        @Schema(description = "종료 시각", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        LocalDateTime endedAt,

        @Schema(description = "총 시간 (초)", example = "762", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero
        Integer totalTime,

        @Schema(description = "총 거리 (m)", example = "3200", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero
        Integer distance,

        @Schema(description = "평균 페이스", example = "06:32", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String avgPace,

        @Schema(description = "칼로리 (클라 계산값)", example = "312", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero
        Integer calories
) {
}
