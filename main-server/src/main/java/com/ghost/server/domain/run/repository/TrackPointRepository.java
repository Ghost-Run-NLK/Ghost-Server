package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.TrackPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {

    @Query("select max(tp.t) from TrackPoint tp where tp.runSession.id = :runSessionId")
    Optional<Integer> findMaxTByRunSessionId(@Param("runSessionId") Long runSessionId);

    List<TrackPoint> findAllByRunSessionIdOrderByTAsc(Long runSessionId);
}
