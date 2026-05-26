package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.RunStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "러닝 세션 종료 응답")
public record RunStopResponse(
        @Schema(description = "러닝 세션 ID", example = "run_2") String runId,
        @Schema(description = "세션 상태", example = "COMPLETED") RunStatus status,
        @Schema(description = "본인의 같은 코스 최단 기록 갱신 여부", example = "true") boolean isNewRecord,
        @Schema(description = "같은 코스 내 본인 순위 (1부터)", example = "1") long rank
) {
}
