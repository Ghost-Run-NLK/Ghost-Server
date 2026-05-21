package com.ghost.server.domain.run.controller;

import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.domain.run.dto.ActiveRunResponse;
import com.ghost.server.domain.run.dto.LocationBatchRequest;
import com.ghost.server.domain.run.dto.LocationBatchResponse;
import com.ghost.server.domain.run.dto.RunDetailResponse;
import com.ghost.server.domain.run.dto.RunStartRequest;
import com.ghost.server.domain.run.dto.RunStartResponse;
import com.ghost.server.domain.run.dto.RunStopRequest;
import com.ghost.server.domain.run.dto.RunStopResponse;
import com.ghost.server.domain.run.service.LocationBatchService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Run", description = "러닝 세션 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runs")
public class RunController {

    private final RunSessionService runSessionService;
    private final LocationBatchService locationBatchService;

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
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody RunStartRequest request
    ) {
        return runSessionService.start(currentUserId, request);
    }

    @Operation(
            summary = "위치 배치 수신",
            description = "ACTIVE 세션에 위치 포인트 배치를 저장한다. t 오름차순 정렬 + 중복 t 제거 후 신규만 INSERT."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수신 성공 (receivedCount = 실제 INSERT 수)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "러닝 세션을 찾을 수 없거나 본인 세션이 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"러닝 세션을 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "세션이 ACTIVE 상태가 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_NOT_ACTIVE",
                                    value = "{\"code\": 409, \"message\": \"진행 중인 러닝 세션이 아닙니다\", \"data\": null}"
                            )
                    )
            )
    })
    @PostMapping("/{runId}/locations")
    public LocationBatchResponse receiveLocations(
            @AuthenticationPrincipal Long currentUserId,
            @Parameter(description = "러닝 세션 ID", example = "run_1", required = true)
            @PathVariable String runId,
            @Valid @RequestBody LocationBatchRequest request
    ) {
        return locationBatchService.receive(currentUserId, runId, request);
    }

    @Operation(
            summary = "러닝 세션 종료",
            description = "ACTIVE 세션을 COMPLETED로 전이하고 요약(시간/거리/페이스/칼로리)을 저장한다. 응답에 본인 순위와 신기록 여부 포함."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "종료 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "러닝 세션을 찾을 수 없거나 본인 세션이 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_NOT_FOUND",
                                    value = "{\"code\": 404, \"message\": \"러닝 세션을 찾을 수 없습니다\", \"data\": null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "세션이 ACTIVE 상태가 아님",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "RUN_NOT_ACTIVE",
                                    value = "{\"code\": 409, \"message\": \"진행 중인 러닝 세션이 아닙니다\", \"data\": null}"
                            )
                    )
            )
    })
    @PatchMapping("/{runId}/stop")
    public RunStopResponse stop(
            @AuthenticationPrincipal Long currentUserId,
            @Parameter(description = "러닝 세션 ID", example = "run_1", required = true)
            @PathVariable String runId,
            @Valid @RequestBody RunStopRequest request
    ) {
        return runSessionService.stop(currentUserId, runId, request);
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
    public ActiveRunResponse active(@AuthenticationPrincipal Long currentUserId) {
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
