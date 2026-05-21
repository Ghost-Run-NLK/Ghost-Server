package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunSessionRepository extends JpaRepository<RunSession, Long> {

    Optional<RunSession> findFirstByUserIdAndStatus(Long userId, RunStatus status);

    boolean existsByUserIdAndStatus(Long userId, RunStatus status);
}
