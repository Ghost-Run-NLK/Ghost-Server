package com.ghost.server.domain.run.controller;

import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.domain.run.dto.ActiveRunResponse;
import com.ghost.server.domain.run.dto.RunDetailResponse;
import com.ghost.server.domain.run.dto.RunStartRequest;
import com.ghost.server.domain.run.dto.RunStartResponse;
import com.ghost.server.domain.run.service.RunSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Run", description = "러닝 세션 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runs")
public class RunController {

    private final RunSessionService runSessionService;

    @Operation(
            summary = "러닝 세션 시작",
            description = "코스와 (선택적으로) 고스트를 지정해 ACTIVE 세션을 만든다. 고스트가 있으면 trackPoints 전체를 응답에 포함한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "세션 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "USER / COURSE / GHOST_RUN 미존재",
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
                    description = "이미 진행 중인 세션 존재",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_ALREADY_ACTIVE",
                                    value = "{\"code\": 409, \"message\": \"이미 진행 중인 러닝 세션이 있습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @PostMapping
    public RunStartResponse start(
            @Parameter(description = "현재 유저 ID (auth PR 전 임시 헤더)", example = "1", required = true)
            @RequestHeader("X-User-Id") Long currentUserId,
            @Valid @RequestBody RunStartRequest request
    ) {
        return runSessionService.start(currentUserId, request);
    }

    @Operation(
            summary = "진행 중인 세션 조회",
            description = "현재 유저의 ACTIVE 세션을 반환한다. 없으면 응답 본문이 null."
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (ACTIVE 없으면 body 가 null)"
            )
    )
    @GetMapping("/active")
    public ActiveRunResponse active(
            @Parameter(description = "현재 유저 ID (auth PR 전 임시 헤더)", example = "1", required = true)
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return runSessionService.findActive(currentUserId);
    }

    @Operation(
            summary = "러닝 기록 상세 조회",
            description = "러닝 세션의 요약 + trackPoints + ghost 요약을 반환한다. 누구나 조회 가능."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "러닝 세션을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"러닝 세션을 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @GetMapping("/{runId}")
    public RunDetailResponse detail(
            @Parameter(description = "러닝 세션 ID", example = "run_1", required = true)
            @PathVariable String runId
    ) {
        return runSessionService.findById(runId);
    }
}
