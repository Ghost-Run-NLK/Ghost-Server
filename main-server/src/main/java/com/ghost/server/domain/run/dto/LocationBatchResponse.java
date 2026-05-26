package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위치 배치 수신 응답 (현재까지 누적된 거리/평균 페이스)")
public record LocationBatchResponse(
        @Schema(description = "지금까지 누적된 거리 (m)", example = "3200") int distance,
        @Schema(description = "지금까지 평균 페이스 (MM:SS / km, distance 0이면 \"00:00\")", example = "06:32")
        String avgPace
) {
}
