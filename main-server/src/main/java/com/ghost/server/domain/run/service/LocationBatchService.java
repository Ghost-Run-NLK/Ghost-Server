package com.ghost.server.domain.run.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.run.dto.LocationBatchRequest;
import com.ghost.server.domain.run.dto.LocationBatchResponse;
import com.ghost.server.domain.run.dto.LocationPointDto;
import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import com.ghost.server.domain.run.entity.TrackPoint;
import com.ghost.server.domain.run.repository.RunSessionRepository;
import com.ghost.server.domain.run.repository.TrackPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LocationBatchService {

    private static final String RUN_ID_PREFIX = "run_";

    private final RunSessionRepository runSessionRepository;
    private final TrackPointRepository trackPointRepository;

    @Transactional
    public LocationBatchResponse receive(Long currentUserId, String runIdParam, LocationBatchRequest request) {
        Long runId = PublicIdCodec.decode(RUN_ID_PREFIX, runIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));
        RunSession run = runSessionRepository.findById(runId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));

        // 소유자 검증 — 다른 유저의 세션은 RUN_NOT_FOUND로 통일 (존재 노출 방지)
        if (!run.getUser().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.RUN_NOT_FOUND);
        }
        if (run.getStatus() != RunStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.RUN_NOT_ACTIVE);
        }

        // 1) 요청 내부 elapsedSec 중복 제거 (먼저 들어온 값 유지) + 오름차순 정렬
        LinkedHashMap<Integer, LocationPointDto> sortedUnique = request.points().stream()
                .sorted(Comparator.comparingInt(LocationPointDto::elapsedSec))
                .collect(LinkedHashMap::new,
                        (m, p) -> m.putIfAbsent(p.elapsedSec(), p),
                        LinkedHashMap::putAll);

        // 2) DB 기존 elapsedSec 조회해서 제외 후 신규만 저장
        if (!sortedUnique.isEmpty()) {
            Set<Integer> existing = new HashSet<>(
                    trackPointRepository.findExistingElapsedSecs(run.getId(), sortedUnique.keySet()));

            List<TrackPoint> toSave = sortedUnique.values().stream()
                    .filter(p -> !existing.contains(p.elapsedSec()))
                    .map(p -> TrackPoint.builder()
                            .runSession(run)
                            .elapsedSec(p.elapsedSec())
                            .lat(p.lat())
                            .lng(p.lng())
                            .build())
                    .toList();

            trackPointRepository.saveAll(toSave);
        }

        // idle 폐기 스케줄러용 ping
        run.touch(LocalDateTime.now());

        // 3) 누적 distance + avgPace 응답
        List<TrackPoint> allPoints = trackPointRepository.findAllByRunSessionIdOrderByElapsedSecAsc(run.getId());
        int distance = RunMetrics.distanceMeters(allPoints);
        int totalTime = allPoints.isEmpty() ? 0 : allPoints.get(allPoints.size() - 1).getElapsedSec();
        String avgPace = RunMetrics.avgPace(totalTime, distance);

        return new LocationBatchResponse(distance, avgPace);
    }
}
