package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.course.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리더보드 응답의 코스 요약")
public record LeaderboardCourseDto(
        @Schema(description = "코스 ID", example = "course_001") String courseId,
        @Schema(description = "코스 이름", example = "성성호수 공원") String name,
        @Schema(description = "총 거리 (m)", example = "3500") int distance
) {
    public static LeaderboardCourseDto from(Course course) {
        return new LeaderboardCourseDto(
                "course_" + course.getId(),
                course.getName(),
                course.getDistance()
        );
    }
}
