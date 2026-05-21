package com.ghost.server.domain.course.dto;

import java.util.List;

public record CourseListResponse(List<CourseSummaryDto> courses) {
}
