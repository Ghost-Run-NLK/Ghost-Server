package com.ghost.server.domain.run.entity;

import com.ghost.server.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "track_points",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_track_run_t",
                columnNames = {"run_session_id", "t"}
        )
)
public class TrackPoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_session_id", nullable = false)
    private RunSession runSession;

    @Column(nullable = false)
    private int t;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(nullable = false)
    private double speed;

    @Builder
    private TrackPoint(RunSession runSession, int t, double lat, double lng, double speed) {
        this.runSession = runSession;
        this.t = t;
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
    }
}
