package com.ghost.server.domain.course.controller;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.course.dto.CourseDetailResponse;
import com.ghost.server.domain.course.dto.CourseListResponse;
import com.ghost.server.domain.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Course", description = "코스 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private static final String COURSE_ID_PREFIX = "course_";

    private final CourseService courseService;

    @Operation(
            summary = "코스 리스트 조회",
            description = "관리자가 등록한 전체 코스를 반환한다."
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    )
    @GetMapping
    public CourseListResponse list() {
        return courseService.findAll();
    }

    @Operation(
            summary = "코스 상세 조회",
            description = "코스 메타 정보와 관리자 설정 경로 좌표(routePoints)를 반환한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "코스를 찾을 수 없음 (잘못된 형식 / 미존재)",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "COURSE_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"코스를 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @GetMapping("/{courseId}")
    public CourseDetailResponse detail(
            @Parameter(description = "코스 ID (예: course_1)", example = "course_1", required = true)
            @PathVariable String courseId
    ) {
        Long id = PublicIdCodec.decode(COURSE_ID_PREFIX, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        return courseService.findById(id);
    }
}
