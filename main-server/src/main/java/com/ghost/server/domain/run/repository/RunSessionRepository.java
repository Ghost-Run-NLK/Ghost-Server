package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RunSessionRepository extends JpaRepository<RunSession, Long> {

    Optional<RunSession> findByUserIdAndStatus(Long userId, RunStatus status);

    List<RunSession> findAllByStatusAndLastLocationAtBefore(RunStatus status, LocalDateTime threshold);

    boolean existsByCourseId(Long courseId);

    long countByCourseIdAndStatusAndTotalTimeLessThan(Long courseId, RunStatus status, int totalTime);

    Optional<RunSession> findFirstByCourseIdAndUserIdAndStatusAndIdNotOrderByTotalTimeAsc(
            Long courseId, Long userId, RunStatus status, Long excludeId);

    List<RunSession> findTop10ByCourseIdAndStatusOrderByTotalTimeAscEndedAtAsc(
            Long courseId, RunStatus status);
}
