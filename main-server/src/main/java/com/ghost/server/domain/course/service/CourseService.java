package com.ghost.server.domain.course.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.domain.course.dto.CourseDetailResponse;
import com.ghost.server.domain.course.dto.CourseListResponse;
import com.ghost.server.domain.course.dto.CourseSummaryDto;
import com.ghost.server.domain.course.entity.Course;
import com.ghost.server.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseListResponse findAll() {
        List<CourseSummaryDto> courses = courseRepository.findAll().stream()
                .map(CourseSummaryDto::from)
                .toList();
        return new CourseListResponse(courses);
    }

    public CourseDetailResponse findById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        return CourseDetailResponse.from(course);
    }
}
