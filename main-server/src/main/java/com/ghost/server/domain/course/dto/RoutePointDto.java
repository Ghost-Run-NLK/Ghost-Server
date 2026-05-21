package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.CourseRoutePoint;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코스 경로 좌표 (관리자 설정)")
public record RoutePointDto(
        @Schema(description = "위도", example = "36.8097") double lat,
        @Schema(description = "경도", example = "127.0079") double lng
) {
    public static RoutePointDto from(CourseRoutePoint point) {
        return new RoutePointDto(point.getLat(), point.getLng());
    }
}
