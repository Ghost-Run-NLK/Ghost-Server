package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "코스 리스트 항목 (지도에 라인 그리기 위한 좌표 포함)")
public record CourseSummaryDto(
        @Schema(description = "코스 ID", example = "course_001") String courseId,
        @Schema(description = "코스 이름", example = "성성호수 공원") String name,
        @Schema(description = "주소", example = "천안시 서북구 천안대로 1223-24") String address,
        @Schema(description = "총 거리 (m)", example = "3500") int distance,
        @Schema(description = "경로 좌표 (sequence 오름차순)") List<RoutePointDto> routePoints
) {
    public static CourseSummaryDto from(Course course) {
        List<RoutePointDto> points = course.getRoutePoints().stream()
                .map(RoutePointDto::from)
                .toList();
        return new CourseSummaryDto(
                "course_" + course.getId(),
                course.getName(),
                course.getAddress(),
                course.getDistance(),
                points
        );
    }
}
