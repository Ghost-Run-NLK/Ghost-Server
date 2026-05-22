package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.RunStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "러닝 기록 상세 응답")
public record RunDetailResponse(
        @Schema(description = "러닝 세션 ID", example = "run_xyz789") String runId,
        @Schema(description = "세션 상태", example = "COMPLETED") RunStatus status,
        @Schema(description = "기록 소유자") RunOwnerDto user,
        @Schema(description = "시작 시각") LocalDateTime startedAt,
        @Schema(description = "종료 시각 (미완료면 null)", nullable = true) LocalDateTime endedAt,
        @Schema(description = "총 시간 (초)", example = "762", nullable = true) Integer totalTime,
        @Schema(description = "총 거리 (m)", example = "3200", nullable = true) Integer distance,
        @Schema(description = "평균 페이스", example = "06:32", nullable = true) String avgPace,
        @Schema(description = "칼로리", example = "312", nullable = true) Integer calories,
        @Schema(description = "고스트 정보 (없으면 null)", nullable = true) GhostSummaryDto ghost,
        @Schema(description = "위치 포인트 (t 오름차순)") List<TrackPointDto> trackPoints
) {
}
