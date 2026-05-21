package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "리더보드 응답 (상위 10등)")
public record LeaderboardResponse(
        @Schema(description = "코스 요약") LeaderboardCourseDto course,
        @Schema(description = "상위 10등 엔트리 (rank 오름차순)") List<LeaderboardEntry> entries
) {
}
