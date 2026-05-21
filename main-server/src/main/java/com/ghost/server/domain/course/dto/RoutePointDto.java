package com.ghost.server.domain.course.dto;

import com.ghost.server.domain.course.entity.CourseRoutePoint;

public record RoutePointDto(double lat, double lng) {
    public static RoutePointDto from(CourseRoutePoint point) {
        return new RoutePointDto(point.getLat(), point.getLng());
    }
}
