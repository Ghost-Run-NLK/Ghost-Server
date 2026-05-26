package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "러닝 세션 시작 요청 (고스트 필수)")
public record RunStartRequest(
        @Schema(description = "코스 ID", example = "course_001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String courseId,

        @Schema(description = "고스트 기록 ID", example = "run_abc123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String ghostRunId
) {
}
