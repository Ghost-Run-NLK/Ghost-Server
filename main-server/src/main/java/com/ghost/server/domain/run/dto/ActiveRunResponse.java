package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.RunStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "진행 중인 러닝 세션 응답 (없으면 null 반환)")
public record ActiveRunResponse(
        @Schema(description = "러닝 세션 ID", example = "run_xyz789") String runId,
        @Schema(description = "세션 상태", example = "ACTIVE") RunStatus status,
        @Schema(description = "시작 시각") LocalDateTime startedAt,
        @Schema(description = "마지막으로 수신된 위치의 경과 시간 (초)", example = "120") int lastReceivedT
) {
}
