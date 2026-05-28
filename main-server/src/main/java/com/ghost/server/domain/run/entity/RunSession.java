package com.ghost.server.domain.run.entity;

import com.ghost.server.common.entity.BaseEntity;
import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "run_sessions",
        indexes = {
                @Index(name = "idx_run_leaderboard", columnList = "course_id, status, total_time"),
                @Index(name = "idx_run_user_status", columnList = "user_id, status")
        }
)
public class RunSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ghost_run_id")
    private RunSession ghostRun;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RunStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "total_time")
    private Integer totalTime;

    private Integer distance;

    @Column(name = "avg_pace", length = 8)
    private String avgPace;

    @OneToMany(mappedBy = "runSession", cascade = CascadeType.ALL)
    private List<TrackPoint> trackPoints = new ArrayList<>();

    @Builder
    private RunSession(User user, Course course, RunSession ghostRun,
                       RunStatus status, LocalDateTime startedAt) {
        this.user = user;
        this.course = course;
        this.ghostRun = ghostRun;
        this.status = status;
        this.startedAt = startedAt;
    }

    public void complete(LocalDateTime endedAt,
                         int totalTime,
                         int distance,
                         String avgPace) {
        this.status = RunStatus.COMPLETED;
        this.endedAt = endedAt;
        this.totalTime = totalTime;
        this.distance = distance;
        this.avgPace = avgPace;
    }

    public void abandon(LocalDateTime endedAt) {
        this.status = RunStatus.ABANDONED;
        this.endedAt = endedAt;
    }
}
