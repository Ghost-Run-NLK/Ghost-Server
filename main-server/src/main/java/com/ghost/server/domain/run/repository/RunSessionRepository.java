package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.RunSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunSessionRepository extends JpaRepository<RunSession, Long> {
}
