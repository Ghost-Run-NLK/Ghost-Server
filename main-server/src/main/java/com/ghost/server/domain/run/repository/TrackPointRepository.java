package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.TrackPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {
}
