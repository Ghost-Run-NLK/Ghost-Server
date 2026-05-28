package com.ghost.server.domain.run.repository;

import com.ghost.server.domain.run.entity.TrackPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {

    @Query("select max(tp.elapsedSec) from TrackPoint tp where tp.runSession.id = :runSessionId")
    Optional<Integer> findMaxElapsedSecByRunSessionId(@Param("runSessionId") Long runSessionId);

    List<TrackPoint> findAllByRunSessionIdOrderByElapsedSecAsc(Long runSessionId);

    @Query("select tp.elapsedSec from TrackPoint tp where tp.runSession.id = :runSessionId and tp.elapsedSec in :elapsedSecs")
    List<Integer> findExistingElapsedSecs(@Param("runSessionId") Long runSessionId,
                                          @Param("elapsedSecs") Collection<Integer> elapsedSecs);

    @Modifying
    @Query("delete from TrackPoint tp where tp.runSession.id = :runSessionId")
    void deleteAllByRunSessionId(@Param("runSessionId") Long runSessionId);
}
