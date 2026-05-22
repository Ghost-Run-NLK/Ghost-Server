package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "위치 배치 수신 요청")
public record LocationBatchRequest(
        @Schema(description = "위치 포인트 배치 (1~1000개)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Size(min = 1, max = 1000, message = "points는 1개 이상 1000개 이하여야 합니다")
        @Valid
        List<LocationPointDto> points
) {
}
