package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.Course;

public record CourseSummaryDto(
        String courseId,
        String name,
        String address,
        int distance,
        double centerLat,
        double centerLng
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
