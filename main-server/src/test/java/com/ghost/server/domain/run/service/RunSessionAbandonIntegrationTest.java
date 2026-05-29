package com.ghost.server.domain.run.service;

import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.course.repository.CourseRepository;
import com.ghost.server.domain.run.dto.RunStartRequest;
import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import com.ghost.server.domain.run.entity.TrackPoint;
import com.ghost.server.domain.run.repository.RunSessionRepository;
import com.ghost.server.domain.run.repository.TrackPointRepository;
import com.ghost.server.domain.user.entity.SocialProvider;
import com.ghost.server.domain.user.entity.User;
import com.ghost.server.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RunSessionAbandonIntegrationTest {

    @Autowired RunSessionService runSessionService;
    @Autowired RunSessionAbandonScheduler scheduler;
    @Autowired RunSessionRepository runSessionRepository;
    @Autowired TrackPointRepository trackPointRepository;
    @Autowired UserRepository userRepository;
    @Autowired CourseRepository courseRepository;

    @PersistenceContext EntityManager em;

    @Test
    @DisplayName("새 런 시작 시 기존 ACTIVE 가 ABANDONED 로 전이되고 그 TrackPoint 가 모두 삭제된다")
    void start_abandons_existing_active_and_deletes_track_points() {
        User runner = userRepository.save(User.builder()
                .socialProvider(SocialProvider.KAKAO)
                .socialId("runner-1")
                .nickname("러너")
                .build());
        User ghostOwner = userRepository.save(User.builder()
                .socialProvider(SocialProvider.KAKAO)
                .socialId("ghost-owner-1")
                .nickname("고스트주인")
                .build());

        Course course = courseRepository.save(Course.builder()
                .name("테스트코스")
                .address("어딘가")
                .distance(1000)
                .startLat(36.0).startLng(127.0)
                .endLat(36.01).endLng(127.01)
                .build());

        // ghost 후보: 다른 유저의 COMPLETED 런
        RunSession ghostRun = runSessionRepository.save(RunSession.builder()
                .user(ghostOwner)
                .course(course)
                .status(RunStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusHours(1))
                .lastLocationAt(LocalDateTime.now().minusHours(1))
                .build());
        ghostRun.complete(LocalDateTime.now().minusMinutes(50), 600, 1000, "10:00");

        // 기존 ACTIVE 런 + TrackPoint 3개
        RunSession active = runSessionRepository.save(RunSession.builder()
                .user(runner)
                .course(course)
                .status(RunStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusMinutes(5))
                .lastLocationAt(LocalDateTime.now().minusMinutes(5))
                .build());
        trackPointRepository.saveAll(List.of(
                TrackPoint.builder().runSession(active).elapsedSec(0).lat(36.0).lng(127.0).build(),
                TrackPoint.builder().runSession(active).elapsedSec(5).lat(36.001).lng(127.001).build(),
                TrackPoint.builder().runSession(active).elapsedSec(10).lat(36.002).lng(127.002).build()
        ));
        Long activeId = active.getId();
        em.flush();
        em.clear();

        runSessionService.start(runner.getId(),
                new RunStartRequest("course_" + course.getId(), "run_" + ghostRun.getId()));

        em.flush();
        em.clear();

        RunSession after = runSessionRepository.findById(activeId).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(RunStatus.ABANDONED);
        assertThat(after.getEndedAt()).isNotNull();
        assertThat(trackPointRepository.findAllByRunSessionIdOrderByElapsedSecAsc(activeId))
                .isEmpty();
    }

    @Test
    @DisplayName("스케줄러가 idle ACTIVE 를 ABANDONED 로 전이하고 그 TrackPoint 를 삭제한다")
    void scheduler_abandons_idle_active_and_deletes_track_points() {
        User user = userRepository.save(User.builder()
                .socialProvider(SocialProvider.KAKAO)
                .socialId("idle-runner")
                .nickname("idle러너")
                .build());
        Course course = courseRepository.save(Course.builder()
                .name("idle코스")
                .address("어딘가")
                .distance(1000)
                .startLat(36.0).startLng(127.0)
                .endLat(36.01).endLng(127.01)
                .build());

        // 임계값(30초)보다 훨씬 과거에 마지막 위치 수신
        LocalDateTime longAgo = LocalDateTime.now().minusMinutes(5);
        RunSession active = runSessionRepository.save(RunSession.builder()
                .user(user)
                .course(course)
                .status(RunStatus.ACTIVE)
                .startedAt(longAgo)
                .lastLocationAt(longAgo)
                .build());
        trackPointRepository.saveAll(List.of(
                TrackPoint.builder().runSession(active).elapsedSec(0).lat(36.0).lng(127.0).build(),
                TrackPoint.builder().runSession(active).elapsedSec(5).lat(36.001).lng(127.001).build()
        ));
        Long activeId = active.getId();
        em.flush();
        em.clear();

        scheduler.markIdleRunsAsAbandoned();

        em.flush();
        em.clear();

        RunSession after = runSessionRepository.findById(activeId).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(RunStatus.ABANDONED);
        assertThat(after.getEndedAt()).isNotNull();
        assertThat(trackPointRepository.findAllByRunSessionIdOrderByElapsedSecAsc(activeId))
                .isEmpty();
    }
}
