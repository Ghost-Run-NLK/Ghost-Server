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

        // 1) 요청 내부 t 중복 제거 (먼저 들어온 값 유지) + t 오름차순 정렬
        LinkedHashMap<Integer, LocationPointDto> sortedUnique = request.points().stream()
                .sorted(Comparator.comparingInt(LocationPointDto::t))
                .collect(LinkedHashMap::new,
                        (m, p) -> m.putIfAbsent(p.t(), p),
                        LinkedHashMap::putAll);

        if (sortedUnique.isEmpty()) {
            int last = trackPointRepository.findMaxTByRunSessionId(run.getId()).orElse(0);
            return new LocationBatchResponse(0, last);
        }

        // 2) DB 기존 t 조회해서 제외
        Set<Integer> existing = new HashSet<>(
                trackPointRepository.findExistingTs(run.getId(), sortedUnique.keySet()));

        List<TrackPoint> toSave = sortedUnique.values().stream()
                .filter(p -> !existing.contains(p.t()))
                .map(p -> TrackPoint.builder()
                        .runSession(run)
                        .t(p.t())
                        .lat(p.lat())
                        .lng(p.lng())
                        .speed(p.speed())
                        .build())
                .toList();

        trackPointRepository.saveAll(toSave);

        int lastReceivedT = trackPointRepository.findMaxTByRunSessionId(run.getId()).orElse(0);
        return new LocationBatchResponse(toSave.size(), lastReceivedT);
    }
}
