package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "러닝 세션 종료 요청 (서버 계산 불가한 클라 책임 값만)")
public record RunStopRequest(
        @Schema(description = "칼로리 (클라 계산값 — HRM/체중 등 클라 입력 의존)",
                example = "312", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero
        Integer calories
) {
}
