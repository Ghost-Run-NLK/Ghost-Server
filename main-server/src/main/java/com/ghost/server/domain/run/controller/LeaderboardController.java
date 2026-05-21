package com.ghost.server.domain.run.controller;

import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.domain.run.dto.LeaderboardResponse;
import com.ghost.server.domain.run.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leaderboard", description = "코스 리더보드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Operation(
            summary = "코스 리더보드 조회",
            description = "코스의 상위 10등 COMPLETED 기록을 반환한다. 본인 기록 entry는 isMe=true."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
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
    @GetMapping("/{courseId}/leaderboard")
    public LeaderboardResponse leaderboard(
            @Parameter(description = "코스 ID", example = "course_1", required = true)
            @PathVariable String courseId,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return leaderboardService.find(courseId, currentUserId);
    }
}
