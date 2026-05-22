package com.ghost.server.domain.run.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.util.GeoUtils;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.course.repository.CourseRepository;
import com.ghost.server.domain.run.dto.ActiveRunResponse;
import com.ghost.server.domain.run.dto.GhostStartDto;
import com.ghost.server.domain.run.dto.GhostSummaryDto;
import com.ghost.server.domain.run.dto.RunDetailResponse;
import com.ghost.server.domain.run.dto.RunOwnerDto;
import com.ghost.server.domain.run.dto.RunStartRequest;
import com.ghost.server.domain.run.dto.RunStartResponse;
import com.ghost.server.domain.run.dto.RunStopRequest;
import com.ghost.server.domain.run.dto.RunStopResponse;
import com.ghost.server.domain.run.dto.TrackPointDto;
import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import com.ghost.server.domain.run.entity.TrackPoint;
import com.ghost.server.domain.run.repository.RunSessionRepository;
import com.ghost.server.domain.run.repository.TrackPointRepository;
import com.ghost.server.domain.user.entity.User;
import com.ghost.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RunSessionService {

    private static final String COURSE_ID_PREFIX = "course_";
    private static final String RUN_ID_PREFIX = "run_";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RunSessionRepository runSessionRepository;
    private final TrackPointRepository trackPointRepository;
    private final GhostService ghostService;

    @Transactional
    public RunStartResponse start(Long currentUserId, RunStartRequest request) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Long courseIdValue = PublicIdCodec.decode(COURSE_ID_PREFIX, request.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        Course course = courseRepository.findById(courseIdValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (runSessionRepository.existsByUserIdAndStatus(user.getId(), RunStatus.ACTIVE)) {
            throw new BusinessException(ErrorCode.RUN_ALREADY_ACTIVE);
        }

        RunSession ghostRun = resolveGhost(request.ghostRunId(), course.getId());

        RunSession saved = runSessionRepository.save(
                RunSession.builder()
                        .user(user)
                        .course(course)
                        .ghostRun(ghostRun)
                        .status(RunStatus.ACTIVE)
                        .startedAt(LocalDateTime.now())
                        .build()
        );

        GhostStartDto ghostDto = ghostRun != null ? ghostService.buildStartDto(ghostRun) : null;
        return new RunStartResponse(
                PublicIdCodec.encode(RUN_ID_PREFIX, saved.getId()),
                saved.getStatus(),
                ghostDto
        );
    }

    public ActiveRunResponse findActive(Long currentUserId) {
        return runSessionRepository
                .findFirstByUserIdAndStatus(currentUserId, RunStatus.ACTIVE)
                .map(run -> new ActiveRunResponse(
                        PublicIdCodec.encode(RUN_ID_PREFIX, run.getId()),
                        run.getStatus(),
                        run.getStartedAt(),
                        trackPointRepository.findMaxTByRunSessionId(run.getId()).orElse(0)
                ))
                .orElse(null);
    }

    @Transactional
    public RunStopResponse stop(Long currentUserId, String runIdParam, RunStopRequest request) {
        Long runId = PublicIdCodec.decode(RUN_ID_PREFIX, runIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));
        RunSession run = runSessionRepository.findById(runId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));

        if (!run.getUser().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.RUN_NOT_FOUND);
        }
        if (run.getStatus() != RunStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.RUN_NOT_ACTIVE);
        }

        LocalDateTime endedAt = LocalDateTime.now();
        int totalTime = (int) Math.max(0, Duration.between(run.getStartedAt(), endedAt).getSeconds());
        List<TrackPoint> points = trackPointRepository.findAllByRunSessionIdOrderByTAsc(run.getId());
        int distance = computeDistance(points);
        String avgPace = computeAvgPace(totalTime, distance);

        run.complete(endedAt, totalTime, distance, avgPace, request.calories());

        Long courseId = run.getCourse().getId();
        boolean isNewRecord = runSessionRepository
                .findFirstByCourseIdAndUserIdAndStatusAndIdNotOrderByTotalTimeAsc(
                        courseId, currentUserId, RunStatus.COMPLETED, run.getId())
                .map(prev -> totalTime < prev.getTotalTime())
                .orElse(true);

        long rank = runSessionRepository
                .countByCourseIdAndStatusAndTotalTimeLessThan(courseId, RunStatus.COMPLETED, totalTime) + 1;

        return new RunStopResponse(
                PublicIdCodec.encode(RUN_ID_PREFIX, run.getId()),
                run.getStatus(),
                isNewRecord,
                rank
        );
    }

    private static int computeDistance(List<TrackPoint> points) {
        if (points.size() < 2) {
            return 0;
        }
        double total = 0;
        for (int i = 1; i < points.size(); i++) {
            TrackPoint a = points.get(i - 1);
            TrackPoint b = points.get(i);
            total += GeoUtils.distanceMeters(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        }
        return (int) Math.round(total);
    }

    private static String computeAvgPace(int totalTimeSec, int distanceMeters) {
        if (distanceMeters <= 0 || totalTimeSec <= 0) {
            return "00:00";
        }
        double secPerKm = totalTimeSec / (distanceMeters / 1000.0);
        int min = (int) (secPerKm / 60);
        int sec = (int) Math.round(secPerKm - min * 60.0);
        if (sec == 60) {
            min++;
            sec = 0;
        }
        return String.format("%02d:%02d", min, sec);
    }

    public RunDetailResponse findById(String runIdParam) {
        Long runId = PublicIdCodec.decode(RUN_ID_PREFIX, runIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));
        RunSession run = runSessionRepository.findById(runId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RUN_NOT_FOUND));

        List<TrackPointDto> trackPoints = trackPointRepository
                .findAllByRunSessionIdOrderByTAsc(run.getId()).stream()
                .map(TrackPointDto::from)
                .toList();

        GhostSummaryDto ghost = run.getGhostRun() != null
                ? ghostService.buildSummary(run, run.getGhostRun())
                : null;

        return new RunDetailResponse(
                PublicIdCodec.encode(RUN_ID_PREFIX, run.getId()),
                run.getStatus(),
                RunOwnerDto.from(run.getUser()),
                run.getStartedAt(),
                run.getEndedAt(),
                run.getTotalTime(),
                run.getDistance(),
                run.getAvgPace(),
                run.getCalories(),
                ghost,
                trackPoints
        );
    }

    public boolean existsForCourse(Long courseId) {
        return runSessionRepository.existsByCourseId(courseId);
    }

    private RunSession resolveGhost(String ghostRunIdParam, Long courseId) {
        if (ghostRunIdParam == null || ghostRunIdParam.isBlank()) {
            return null;
        }
        Long ghostId = PublicIdCodec.decode(RUN_ID_PREFIX, ghostRunIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.GHOST_RUN_NOT_FOUND));
        RunSession ghost = runSessionRepository.findById(ghostId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GHOST_RUN_NOT_FOUND));
        if (ghost.getStatus() != RunStatus.COMPLETED || !ghost.getCourse().getId().equals(courseId)) {
            throw new BusinessException(ErrorCode.GHOST_RUN_NOT_FOUND);
        }
        return ghost;
    }
}
