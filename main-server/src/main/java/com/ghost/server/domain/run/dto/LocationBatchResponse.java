package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위치 배치 수신 응답")
public record LocationBatchResponse(
        @Schema(description = "실제 저장된 포인트 수 (중복 제외)", example = "15") int receivedCount,
        @Schema(description = "현재까지 저장된 마지막 포인트의 경과 시간 (초)", example = "30") int lastReceivedT
) {
}
