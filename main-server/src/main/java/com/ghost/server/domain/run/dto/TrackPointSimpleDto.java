package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.TrackPoint;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "고스트 위치 포인트")
public record TrackPointSimpleDto(
        @Schema(description = "러닝 시작 이후 경과 시간 (초)", example = "0") int elapsedSec,
        @Schema(description = "위도", example = "36.8097") double lat,
        @Schema(description = "경도", example = "127.0079") double lng
) {
    public static TrackPointSimpleDto from(TrackPoint tp) {
        return new TrackPointSimpleDto(tp.getElapsedSec(), tp.getLat(), tp.getLng());
    }
}
