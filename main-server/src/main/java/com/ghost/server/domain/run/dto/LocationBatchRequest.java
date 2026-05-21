package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "위치 배치 수신 요청")
public record LocationBatchRequest(
        @Schema(description = "위치 포인트 배치", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<LocationPointDto> points
) {
}
