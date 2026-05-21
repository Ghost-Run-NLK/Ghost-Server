package com.ghost.server.domain.course.entity;

import com.ghost.server.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "course_route_points",
        indexes = @Index(name = "idx_route_course_sequence", columnList = "course_id, sequence")
)
public class CourseRoutePoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Builder
    private CourseRoutePoint(Course course, int sequence, double lat, double lng) {
        this.course = course;
        this.sequence = sequence;
        this.lat = lat;
        this.lng = lng;
    }
}
