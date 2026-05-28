package com.ghost.server.domain.run.controller;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.common.util.PublicIdCodec;
import com.ghost.server.domain.run.dto.LocationBatchRequest;
import com.ghost.server.domain.run.dto.LocationBatchResponse;
import com.ghost.server.domain.run.dto.RunStartRequest;
import com.ghost.server.domain.run.dto.RunStartResponse;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Run", description = "러닝 세션 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runs")
public class RunController {

    private static final String USER_ID_PREFIX = "user_";

    private final RunSessionService runSessionService;
    private final LocationBatchService locationBatchService;

    private static Long decodeUserId(String userIdParam) {
        return PublicIdCodec.decode(USER_ID_PREFIX, userIdParam)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Operation(
            summary = "러닝 세션 시작",
            description = "코스와 고스트를 지정해 ACTIVE 세션을 만든다. 고스트의 trackPoints 전체를 응답에 포함한다. " +
                          "동일 유저에 ACTIVE 런이 있으면 자동으로 ABANDONED 처리 후 새 ACTIVE 런을 생성한다 (유저당 ACTIVE 1개 정책)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "세션 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = RunStartResponse.class),
                            examples = @ExampleObject(
                                    name = "with-ghost",
                                    value = """
                                            {
                                              "runId": "run_2",
                                              "status": "ACTIVE",
                                              "ghost": {
                                                "runId": "run_1",
                                                "nickname": "달리기 장인",
                                                "avatarUrl": "/avatars/bear.png",
                                                "totalTime": 762,
                                                "avgPace": "06:32",
                                                "trackPoints": [
                                                  { "elapsedSec": 0,   "lat": 36.8097, "lng": 127.0079 },
                                                  { "elapsedSec": 2,   "lat": 36.8098, "lng": 127.0080 },
                                                  { "elapsedSec": 762, "lat": 36.8210, "lng": 127.0190 }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
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
            )
    })
    @PostMapping
    public RunStartResponse start(
            @Parameter(description = "유저 ID (데모: 로그인 대신 쿼리로 전달)", example = "user_1", required = true)
            @RequestParam("userId") String userId,
            @Valid @RequestBody RunStartRequest request
    ) {
        return runSessionService.start(decodeUserId(userId), request);
    }

    @Operation(
            summary = "위치 배치 수신",
            description = "ACTIVE 세션에 위치 포인트 배치를 저장한다. elapsedSec 오름차순 정렬 + 중복 elapsedSec 제거 후 신규만 INSERT. " +
                    "응답으로 현재까지 누적된 distance(m)와 avgPace(MM:SS/km)를 함께 반환."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수신 성공 (응답 = 누적 distance + 평균 페이스)"
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
            @Parameter(description = "유저 ID (데모: 로그인 대신 쿼리로 전달)", example = "user_1", required = true)
            @RequestParam("userId") String userId,
            @Parameter(description = "러닝 세션 ID", example = "run_1", required = true)
            @PathVariable String runId,
            @Valid @RequestBody LocationBatchRequest request
    ) {
        return locationBatchService.receive(decodeUserId(userId), runId, request);
    }

    @Operation(
            summary = "러닝 세션 종료",
            description = "ACTIVE 세션을 COMPLETED로 전이한다. endedAt/totalTime/distance/avgPace는 서버에서 계산 " +
                    "(now, startedAt 차, trackPoints 누적 Haversine, totalTime/distance 파생). " +
                    "요청 body 없음. 응답에 본인 순위와 신기록 여부 포함."
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
            @Parameter(description = "유저 ID (데모: 로그인 대신 쿼리로 전달)", example = "user_1", required = true)
            @RequestParam("userId") String userId,
            @Parameter(description = "러닝 세션 ID", example = "run_1", required = true)
            @PathVariable String runId
    ) {
        return runSessionService.stop(decodeUserId(userId), runId);
    }
}
