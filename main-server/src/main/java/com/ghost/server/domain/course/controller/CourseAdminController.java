package com.ghost.server.domain.course.controller;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.course.dto.CourseDetailResponse;
import com.ghost.server.domain.course.dto.CourseUpsertRequest;
import com.ghost.server.domain.course.service.CourseAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// TODO: Auth 도메인 구현 시 ADMIN 권한 가드 추가
@Tag(name = "Course Admin", description = "코스 관리자 API (등록/수정/삭제)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/courses")
public class CourseAdminController {

    private static final String COURSE_ID_PREFIX = "course_";

    private final CourseAdminService courseAdminService;

    @Operation(
            summary = "코스 등록",
            description = "메타 정보 + 경로 좌표를 한 번에 받아 새 코스를 만든다."
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "등록 성공 (생성된 코스 상세 반환)"
            )
    )
    @PostMapping
    public CourseDetailResponse create(@Valid @RequestBody CourseUpsertRequest request) {
        return courseAdminService.create(request);
    }

    @Operation(
            summary = "코스 수정",
            description = "메타 정보 + 경로 좌표를 전체 교체한다. 기존 routePoints 는 모두 삭제 후 재삽입."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "코스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "COURSE_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"코스를 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @PutMapping("/{courseId}")
    public CourseDetailResponse update(
            @Parameter(description = "코스 ID", example = "course_1", required = true)
            @PathVariable String courseId,
            @Valid @RequestBody CourseUpsertRequest request
    ) {
        return courseAdminService.update(decodeCourseId(courseId), request);
    }

    @Operation(
            summary = "코스 삭제",
            description = "코스를 삭제한다. 단, RunSession이 참조 중이면 거부."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "코스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "COURSE_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"코스를 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "러닝 기록이 존재하는 코스",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "COURSE_IN_USE",
                                    value = "{\"code\": 409, \"message\": \"러닝 기록이 존재하는 코스는 삭제할 수 없습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    public void delete(
            @Parameter(description = "코스 ID", example = "course_1", required = true)
            @PathVariable String courseId
    ) {
        courseAdminService.delete(decodeCourseId(courseId));
    }

    private static Long decodeCourseId(String courseId) {
        return PublicIdCodec.decode(COURSE_ID_PREFIX, courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }
}
