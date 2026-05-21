package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.RunStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "러닝 세션 시작 응답")
public record RunStartResponse(
        @Schema(description = "신규 러닝 세션 ID", example = "run_xyz789") String runId,
        @Schema(description = "세션 상태", example = "ACTIVE") RunStatus status,
        @Schema(description = "고스트 정보 (선택 시), 없으면 null", nullable = true) GhostStartDto ghost
) {
}
