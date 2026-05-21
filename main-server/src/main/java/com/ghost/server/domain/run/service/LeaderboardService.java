package com.ghost.server.domain.run.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.course.repository.CourseRepository;
import com.ghost.server.domain.run.dto.LeaderboardCourseDto;
import com.ghost.server.domain.run.dto.LeaderboardEntry;
import com.ghost.server.domain.run.dto.LeaderboardResponse;
import com.ghost.server.domain.run.entity.RunSession;
import com.ghost.server.domain.run.entity.RunStatus;
import com.ghost.server.domain.run.repository.RunSessionRepository;
import com.ghost.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

    private static final String COURSE_ID_PREFIX = "course_";
    private static final String RUN_ID_PREFIX = "run_";
    private static final String USER_ID_PREFIX = "user_";

    private final CourseRepository courseRepository;
    private final RunSessionRepository runSessionRepository;

    public LeaderboardResponse find(String courseIdParam, Long currentUserId) {
        Long courseId = PublicIdCodec.decode(COURSE_ID_PREFIX, courseIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        List<RunSession> top = runSessionRepository
                .findTop10ByCourseIdAndStatusOrderByTotalTimeAscEndedAtAsc(course.getId(), RunStatus.COMPLETED);

        List<LeaderboardEntry> entries = IntStream.range(0, top.size())
                .mapToObj(i -> toEntry(i + 1, top.get(i), currentUserId))
                .toList();

        return new LeaderboardResponse(LeaderboardCourseDto.from(course), entries);
    }

    private LeaderboardEntry toEntry(int rank, RunSession run, Long currentUserId) {
        User user = run.getUser();
        boolean isMe = currentUserId.equals(user.getId());
        return new LeaderboardEntry(
                rank,
                PublicIdCodec.encode(RUN_ID_PREFIX, run.getId()),
                PublicIdCodec.encode(USER_ID_PREFIX, user.getId()),
                user.getNickname(),
                user.getAvatarUrl(),
                run.getTotalTime(),
                run.getAvgPace(),
                run.getEndedAt(),
                isMe
        );
    }
}
