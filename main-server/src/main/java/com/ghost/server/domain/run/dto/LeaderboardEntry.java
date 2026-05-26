package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "리더보드 항목")
public record LeaderboardEntry(
        @Schema(description = "순위 (1부터)", example = "1") int rank,
        @Schema(description = "러닝 세션 ID", example = "run_1") String runId,
        @Schema(description = "유저 ID", example = "user_1") String userId,
        @Schema(description = "닉네임", example = "달리기 장인") String nickname,
        @Schema(description = "아바타 URL", example = "/avatars/bear.png", nullable = true) String avatarUrl,
        @Schema(description = "총 시간 (초)", example = "762") int totalTime,
        @Schema(description = "평균 페이스", example = "06:32") String avgPace,
        @Schema(description = "완료 시각") LocalDateTime completedAt,
        @Schema(description = "내 기록 여부", example = "false") boolean isMe
) {
}
