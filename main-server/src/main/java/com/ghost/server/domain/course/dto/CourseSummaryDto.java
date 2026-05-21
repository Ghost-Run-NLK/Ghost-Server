package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "코스 리스트 항목")
public record CourseSummaryDto(
        @Schema(description = "코스 ID", example = "course_001") String courseId,
        @Schema(description = "코스 이름", example = "성성호수 공원") String name,
        @Schema(description = "주소", example = "천안시 서북구 천안대로 1223-24") String address,
        @Schema(description = "총 거리 (m)", example = "3500") int distance,
        @Schema(description = "썸네일 중심 위도", example = "36.8097") double centerLat,
        @Schema(description = "썸네일 중심 경도", example = "127.0079") double centerLng
) {
    public static CourseSummaryDto from(Course course) {
        return new CourseSummaryDto(
                "course_" + course.getId(),
                course.getName(),
                course.getAddress(),
                course.getDistance(),
                course.getCenterLat(),
                course.getCenterLng()
        );
    }
}
