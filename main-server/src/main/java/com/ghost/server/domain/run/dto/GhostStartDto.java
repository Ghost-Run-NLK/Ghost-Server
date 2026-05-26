package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "러닝 시작 응답의 고스트 정보 (전체 trackPoints 포함)")
public record GhostStartDto(
        @Schema(description = "고스트 기록 ID", example = "run_abc123") String runId,
        @Schema(description = "고스트 닉네임", example = "달리기 장인") String nickname,
        @Schema(description = "아바타 URL", example = "https://example.com/a.png", nullable = true) String avatarUrl,
        @Schema(description = "고스트 총 시간 (초)", example = "762") int totalTime,
        @Schema(description = "고스트 평균 페이스", example = "06:32") String avgPace,
        @Schema(description = "고스트 위치 포인트 (elapsedSec 오름차순)") List<TrackPointSimpleDto> trackPoints
) {
}
