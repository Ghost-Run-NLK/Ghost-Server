package com.ghost.server.domain.run.service;

import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.run.dto.GhostStartDto;
import com.ghost.server.domain.run.dto.TrackPointSimpleDto;
import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.repository.TrackPointRepository;
import com.ghost.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GhostService {

    static final String RUN_ID_PREFIX = "run_";

    private final TrackPointRepository trackPointRepository;

    public GhostStartDto buildStartDto(RunSession ghostRun) {
        User user = ghostRun.getUser();
        List<TrackPointSimpleDto> points = trackPointRepository
                .findAllByRunSessionIdOrderByTAsc(ghostRun.getId()).stream()
                .map(TrackPointSimpleDto::from)
                .toList();
        return new GhostStartDto(
                PublicIdCodec.encode(RUN_ID_PREFIX, ghostRun.getId()),
                user.getNickname(),
                user.getAvatarUrl(),
                ghostRun.getTotalTime(),
                ghostRun.getAvgPace(),
                points
        );
    }
}
