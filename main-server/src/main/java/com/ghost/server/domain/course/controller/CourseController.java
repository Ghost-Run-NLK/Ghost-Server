package com.ghost.server.domain.course.controller;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.domain.course.dto.CourseDetailResponse;
import com.ghost.server.domain.course.dto.CourseListResponse;
import com.ghost.server.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private static final String COURSE_ID_PREFIX = "course_";

    private final CourseService courseService;

    @GetMapping
    public CourseListResponse list() {
        return courseService.findAll();
    }

    @GetMapping("/{courseId}")
    public CourseDetailResponse detail(@PathVariable String courseId) {
        return courseService.findById(parseCourseId(courseId));
    }

    private static Long parseCourseId(String courseId) {
        if (courseId == null || !courseId.startsWith(COURSE_ID_PREFIX)) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
        try {
            return Long.parseLong(courseId.substring(COURSE_ID_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
    }
}
