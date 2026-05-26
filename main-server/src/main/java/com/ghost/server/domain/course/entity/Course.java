package com.ghost.server.domain.course.entity;

import com.ghost.server.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courses")
public class Course extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private int distance;

    @Column(name = "start_lat", nullable = false)
    private double startLat;

    @Column(name = "start_lng", nullable = false)
    private double startLng;

    @Column(name = "end_lat", nullable = false)
    private double endLat;

    @Column(name = "end_lng", nullable = false)
    private double endLng;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<CourseRoutePoint> routePoints = new ArrayList<>();

    @Builder
    private Course(String name, String address, int distance,
                   double startLat, double startLng,
                   double endLat, double endLng) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
    }

    public void update(String name, String address, int distance,
                       double startLat, double startLng,
                       double endLat, double endLng) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
    }
}
