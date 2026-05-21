package com.ghost.server.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "코스 리스트 응답")
public record CourseListResponse(
        @Schema(description = "코스 리스트") List<CourseSummaryDto> courses
) {
}
