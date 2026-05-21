package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.run.entity.TrackPoint;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "러닝 위치 포인트 (속도 포함)")
public record TrackPointDto(
        @Schema(description = "경과 시간 (초)", example = "0") int t,
        @Schema(description = "위도", example = "36.8097") double lat,
        @Schema(description = "경도", example = "127.0079") double lng,
        @Schema(description = "속도 (m/s)", example = "2.8") double speed
) {
    public static TrackPointDto from(TrackPoint tp) {
        return new TrackPointDto(tp.getT(), tp.getLat(), tp.getLng(), tp.getSpeed());
    }
}
