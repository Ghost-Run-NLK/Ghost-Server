package com.ghost.server.domain.run.service;

import com.ghost.server.domain.run.config.RunProperties;
import com.ghost.server.domain.run.entity.RunStatus;
import com.ghost.server.domain.run.repository.RunSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RunSessionAbandonScheduler {

    private final RunSessionRepository runSessionRepository;
    private final RunProperties runProperties;

    @Scheduled(fixedDelayString = "#{ ${ghost.run.scan-interval-seconds} * 1000 }")
    @Transactional
    public void markIdleRunsAsAbandoned() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusSeconds(runProperties.idleThresholdSeconds());
        runSessionRepository
                .findAllByStatusAndLastLocationAtBefore(RunStatus.ACTIVE, threshold)
                .forEach(run -> run.abandon(now));
    }
}
