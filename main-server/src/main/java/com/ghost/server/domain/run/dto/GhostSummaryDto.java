package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "러닝 상세 응답의 고스트 요약")
public record GhostSummaryDto(
        @Schema(description = "고스트 기록 ID", example = "run_abc123") String runId,
        @Schema(description = "고스트 닉네임", example = "달리기 장인") String nickname,
        @Schema(description = "고스트 대비 시간 차이 (초). 음수 = 내가 빠름. 미완료면 null", example = "-18", nullable = true)
        Integer timeDiff
) {
}
