package com.ghost.server.domain.course.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.domain.course.dto.CourseDetailResponse;
import com.ghost.server.domain.course.dto.CourseUpsertRequest;
import com.ghost.server.domain.course.dto.RoutePointDto;
import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.course.entity.CourseRoutePoint;
import com.ghost.server.domain.course.repository.CourseRepository;
import com.ghost.server.domain.run.service.RunSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseAdminService {

    private final CourseRepository courseRepository;
    private final RunSessionService runSessionService;

    public CourseDetailResponse create(CourseUpsertRequest request) {
        Course course = Course.builder()
                .name(request.name())
                .address(request.address())
                .distance(request.distance())
                .startLat(request.startLat())
                .startLng(request.startLng())
                .endLat(request.endLat())
                .endLng(request.endLng())
                .build();
        addRoutePoints(course, request.routePoints());
        Course saved = courseRepository.save(course);
        return CourseDetailResponse.from(saved);
    }

    public CourseDetailResponse update(Long courseId, CourseUpsertRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        course.update(
                request.name(),
                request.address(),
                request.distance(),
                request.startLat(),
                request.startLng(),
                request.endLat(),
                request.endLng()
        );

        course.getRoutePoints().clear();
        addRoutePoints(course, request.routePoints());

        return CourseDetailResponse.from(course);
    }

    public void delete(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        if (runSessionService.existsForCourse(course.getId())) {
            throw new BusinessException(ErrorCode.COURSE_IN_USE);
        }
        courseRepository.delete(course);
    }

    private static void addRoutePoints(Course course, List<RoutePointDto> points) {
        for (int i = 0; i < points.size(); i++) {
            RoutePointDto p = points.get(i);
            course.getRoutePoints().add(
                    CourseRoutePoint.builder()
                            .course(course)
                            .sequence(i)
                            .lat(p.lat())
                            .lng(p.lng())
                            .build()
            );
        }
    }
}
