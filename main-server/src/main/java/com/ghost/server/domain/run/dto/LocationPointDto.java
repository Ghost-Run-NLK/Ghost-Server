package com.ghost.server.domain.run.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "위치 배치 항목")
public record LocationPointDto(
        @Schema(description = "러닝 시작 이후 경과 시간 (초)", example = "2") int elapsedSec,
        @Schema(description = "위도", example = "36.8098") double lat,
        @Schema(description = "경도", example = "127.0080") double lng
) {
}
