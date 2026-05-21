package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.Course;

import java.util.List;

public record CourseDetailResponse(
        String courseId,
        String name,
        String address,
        int distance,
        double startLat,
        double startLng,
        double endLat,
        double endLng,
        List<RoutePointDto> routePoints
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
