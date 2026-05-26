package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "코스 상세 응답")
public record CourseDetailResponse(
        @Schema(description = "코스 ID", example = "course_1") String courseId,
        @Schema(description = "코스 이름", example = "성성호수 공원") String name,
        @Schema(description = "주소", example = "천안시 서북구 천안대로 1223-24") String address,
        @Schema(description = "총 거리 (m)", example = "3500") int distance,
        @Schema(description = "출발지 위도", example = "36.8097") double startLat,
        @Schema(description = "출발지 경도", example = "127.0079") double startLng,
        @Schema(description = "도착지 위도", example = "36.8210") double endLat,
        @Schema(description = "도착지 경도", example = "127.0190") double endLng,
        @Schema(description = "경로 좌표 (sequence 오름차순)") List<RoutePointDto> routePoints
) {
    public static CourseDetailResponse from(Course course) {
        List<RoutePointDto> points = course.getRoutePoints().stream()
                .map(RoutePointDto::from)
                .toList();
        return new CourseDetailResponse(
                "course_" + course.getId(),
                course.getName(),
                course.getAddress(),
                course.getDistance(),
                course.getStartLat(),
                course.getStartLng(),
                course.getEndLat(),
                course.getEndLng(),
                points
        );
    }
}
